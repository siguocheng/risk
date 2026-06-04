package com.riskcontrol.util;


import com.riskcontrol.dao.RoleMapper;
import com.riskcontrol.domain.User;
import com.riskcontrol.domain.vo.TokenUserBean;
import com.riskcontrol.service.IPermissionResourceService;
import com.riskcontrol.service.IUserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * redis工具类，对redis的操作统一在此处理
 *
 * @author Charlie
 * @date 2018/8/30 9:49
 */
@Component
@Slf4j
public class RedisUtil {

    private static JedisPool jedisPoolInstance;

    @Resource
    private JedisPool jedisPool;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    @Lazy
    private IUserService userService;

    @Resource
    private IPermissionResourceService permissionResourceService;

    /**
     * 给存放到redis的token的加上前缀
     */
    public static final String thePrefixOfToken = "robot_token_";
    public static final String REFRESH_TOKEN = "robot_rtoken_";

    /**
     * 初始化redis工具类的静态成员变量
     *
     * @throws LifecycleException 初始化生命周期异常
     * @author Charlie
     * @date 2018/8/30 10:14
     */
    @PostConstruct
    public void initRedisUtil() throws LifecycleException {
        if (jedisPool != null) {
            jedisPoolInstance = jedisPool;
        } else {
            throw new LifecycleException("初始化redisUtil静态成员失败，间接注入异常");
        }
    }

    private static final String closeFailed = "finally close jedis error,msg:";
    private static final String jedisNull = "finally jedis is null";


    /**
     * 从Jedis连接池获取Jedis连接实例
     *
     * @return redis.clients.jedis.Jedis
     * @author sq.ma
     * @date 2020/6/5 9:32 上午
     */
    public static Jedis getJedisResource() {
        return jedisPoolInstance.getResource();
    }


    /**
     * 根据token从redis中获取当前登录人信息
     *
     * @param token 登录系统后返回，用作登录人身份标识
     * @return TokenUserBean
     * @author Charlie
     * @date 2018/8/30 11:48
     */
    public static TokenUserBean getTokenUserBeanFromRedisByToken(String token) {
        TokenUserBean tokenUserBean = null;
        Jedis jedis = null;
        try {
            jedis = jedisPoolInstance.getResource();
            token = thePrefixOfToken + token;
            byte[] bs = jedis.get(token.getBytes());

            if (bs != null) {
                Object obj = SerializeUtil.unserialize(bs);
                if (obj instanceof TokenUserBean) {
                    tokenUserBean = (TokenUserBean) obj;
                } else {
                    jedis.del(token);
                }
            }
            if (tokenUserBean != null) {
                //对于是否修改过权限的查询，进查询一次，查询完毕后就要赋值为false
                Boolean isChangedPermission = tokenUserBean.getIsChangedPermission();
                if (isChangedPermission != null && isChangedPermission) {
                    tokenUserBean.setIsChangedPermission(false);
                    jedis.setrange(token.getBytes(), 0, SerializeUtil.serialize(tokenUserBean));
                }

                resetToken(jedis, tokenUserBean, token);
                tokenUserBean.setIsChangedPermission(isChangedPermission);
            }
        } catch (Exception e) {
            logErrorForBusiness("getTokenUserBeanFromRedisByToken", e);
            throw e;
        } finally {
            closeJedisWithLogging("getTokenUserBeanFromRedisByToken", jedis);
        }
        return tokenUserBean;
    }

    /**
     * 关闭jedis连接的复用方法，且进行日志记录
     */
    public static void closeJedisWithLogging(String methodName, Jedis jedis) {
        if (jedis != null) {
            // 归还连接
            try {
                jedis.close();
            } catch (Exception e) {
                log.error(String.format("%s,%s[%s]", methodName, closeFailed, e.getMessage()), e);
            }
        } else {
            log.error(String.format("%s,%s", methodName, jedisNull));
        }
    }

    /**
     * 这是针对业务层面的报错，从而记录日志
     */
    private static void logErrorForBusiness(String methodName, Exception e) {
        //damon 2019-09-06 23:34 log.error(string,throwable)方法可以把堆栈信息都输出到日志中
        log.error(String.format("%s error,msg:[%s]", methodName, e.getMessage()), e);
    }


    /**
     * 根据refreshToken从redis中获取当前登录人信息
     *
     * @param refreshToken 登录系统后返回，用作登录人身份标识
     * @return TokenUserBean
     * @author xz
     * @date 2019/9/28 11:48
     */
    public static TokenUserBean getTokenUserBeanFromRedisByRefreshToken(String refreshToken) {
        TokenUserBean tokenUserBean = null;
        Jedis jedis = null;
        try {
            jedis = jedisPoolInstance.getResource();
            refreshToken = REFRESH_TOKEN + refreshToken;
            byte[] bs = jedis.get(refreshToken.getBytes());
            if (bs != null) {
                tokenUserBean = (TokenUserBean) SerializeUtil.unserialize(bs);
            }
        } catch (Exception e) {
            logErrorForBusiness("getTokenUserBeanFromRedisByRefreshToken", e);
            throw e;
        } finally {
            closeJedisWithLogging("getTokenUserBeanFromRedisByRefreshToken", jedis);
        }
        return tokenUserBean;
    }

    /**
     * 重置token
     * <p>
     * 如果有效时间快到了，则延长
     * <p>
     * 如果之前是权限发生改变的状态，则将权限标志发生改变设置为false
     *
     * @author sq.ma
     * @date 2019/12/3 下午5:59
     */
    private static void resetToken(Jedis jedis, TokenUserBean tokenUserBean, String tokenWithPrefix) {
        try {
            Long expire = jedis.ttl(tokenWithPrefix);
            if (expire <= JWTUtil.EXPIRE_TTL_TIME) {
                jedis.setex(tokenWithPrefix.getBytes(), JWTUtil.EXPIRE_TIME_15, SerializeUtil.serialize(tokenUserBean));
            }
        } catch (Exception e) {
            logErrorForBusiness("resetExpireForTokenIfNecessary", e);
            throw e;
        }
    }


    public void saveTokenUserBean(TokenUserBean tokenUser, String token) {
        Jedis jedis = null;
        try {
            jedis = jedisPoolInstance.getResource();
            token = thePrefixOfToken + token;
            jedis.setex(token.getBytes(), JWTUtil.EXPIRE_TIME_15, SerializeUtil.serialize(tokenUser));
        } catch (Exception e) {
            logErrorForBusiness("saveTokenUserBean", e);
            throw e;
        } finally {
            closeJedisWithLogging("saveTokenUserBean", jedis);
        }
    }

    public void saveRefreshToken(TokenUserBean tokenUser, String refreshToken) {
        Jedis jedis = null;
        try {
            jedis = jedisPoolInstance.getResource();
            refreshToken = REFRESH_TOKEN + refreshToken;
            //删除老的token 换新的token
            jedis.setex(refreshToken.getBytes(), JWTUtil.EXPIRE_TIME_30, SerializeUtil.serialize(tokenUser));
        } catch (Exception e) {
            logErrorForBusiness("saveRefreshtoken", e);
            throw e;
        } finally {
            closeJedisWithLogging("saveRefreshtoken", jedis);
        }
    }

    public static Long delete(Object key) {
        Jedis jedis = null;
        Long deleteAmount;
        try {
            jedis = jedisPoolInstance.getResource();
            deleteAmount = jedis.del(key.toString().getBytes());
        } catch (Exception e) {
            logErrorForBusiness("delete", e);
            throw e;
        } finally {
            closeJedisWithLogging("delete", jedis);
        }
        return deleteAmount;
    }

    /**
     * damon
     *
     * @param jedis 要求传参jedis对象，上面那个delete()，如果在for循环，存在一个致命的弊端，就是你for几次，它就生成几次jedis对象
     *              这是致命的，很容易导致jedis连接池溢出，然后报错could not get resource from pool
     * @date 2019-07-15
     */
    private static void deleteWithOneJedis(Object key, Jedis jedis) {
        try {
            jedis.del(key.toString().getBytes());
        } catch (Exception e) {
            logErrorForBusiness("deleteWithOneJedis", e);
            throw e;
        }
    }

    /**
     * damon
     *
     * @date 2019-07-08
     * 一旦删除用户，那么去redis找出是thePrefixOfToken这种前缀的key,然后解析使用jwtutil去解析符合条件的key，
     * 如果解析成功，获取userId，然后再与传参的userId比较是否一致，一致的话那么这个key就需要删除
     */
    public static void deleteDataOfUser(Long userId) {
        Jedis jedis = null;
        try {
            jedis = jedisPoolInstance.getResource();
            Set<String> keys = getStringValues(thePrefixOfToken + "*");
            Set<String> tokenSetShouldDelete = new HashSet<>();
            for (String each : keys) {
                //解析token的时候，需要把前缀去掉，token_ 长度=5，下标=5就是从第6个元素开始
                each = each.substring(thePrefixOfToken.length());
                Long id = JWTUtil.getUserId(each);
                if (id == null) {
                    continue;
                }
                if (userId.equals(id)) {
                    tokenSetShouldDelete.add(thePrefixOfToken + each);
                    //不能break
                }
            }
            if (tokenSetShouldDelete.size() > 0) {
                log.info("删除key开始");
                for (String each : tokenSetShouldDelete) {
                    deleteWithOneJedis(each, jedis);
                }
                log.info("删除key结束");
            }
        } catch (Exception e) {
            logErrorForBusiness("deleteDataOfUser", e);
            throw e;
        } finally {
            closeJedisWithLogging("deleteDataOfUser", jedis);
        }
    }

    public static void deleteDataOfUser(List<Long> userIdList) {
        Jedis jedis = null;
        try {
            jedis = jedisPoolInstance.getResource();
            Set<String> keys = getStringValues(thePrefixOfToken + "*");
            Set<String> tokenSetShouldDelete = new HashSet<>();
            for (String each : keys) {
                //解析token的时候，需要把前缀去掉，token_ 长度=5，下标=5就是从第6个元素开始
                each = each.substring(thePrefixOfToken.length());
                Long id = JWTUtil.getUserId(each);
                if (id == null) {
                    continue;
                }
                if (userIdList.contains(id)) {
                    tokenSetShouldDelete.add(thePrefixOfToken + each);
                    //不能break
                }
            }
            if (tokenSetShouldDelete.size() > 0) {
                log.info("删除key开始");
                for (String each : tokenSetShouldDelete) {
                    deleteWithOneJedis(each, jedis);
                }
                log.info("删除key结束");
            }
        } catch (Exception e) {
            logErrorForBusiness("deleteDataOfUser", e);
            throw e;
        } finally {
            closeJedisWithLogging("deleteDataOfUser", jedis);
        }
    }




    public static Map<String, Object> getRedisCurrentInfo() throws IllegalAccessException, InvocationTargetException {
        Map<String, Object> resultMap = new HashMap<>(16);
        Method[] methods = JedisPool.class.getMethods();
        AccessibleObject.setAccessible(methods, true);
        for (Method each : methods) {
            String methodName = each.getName();
            if (!methodName.contains("get") || methodName.equals("getResource") || methodName.equals("getClass")) {
                continue;
            }
            try {
                Object result = each.invoke(jedisPoolInstance);
                methodName = methodName.replace("get", "");
                resultMap.put(methodName, result);
            } catch (IllegalAccessException e) {
                log.error("getRedisCurrentInfo,通过反射访问方法失败");
                throw new IllegalAccessException("通过反射访问方法失败");
            } catch (InvocationTargetException e) {
                logErrorForBusiness("getRedisCurrentInfo", e);
                throw new InvocationTargetException(e);
            }
        }
        return resultMap;
    }

    public static String getOneTokenByUserId(Long userId) {
        Jedis jedis = null;
        String userToken = null;
        try {
            jedis = jedisPoolInstance.getResource();
            Set<String> keys = getStringValues(thePrefixOfToken + "*");
            for (String each : keys) {
                //解析token的时候，需要把前缀去掉，token_ 长度=5，下标=5就是从第6个元素开始
                each = each.substring(thePrefixOfToken.length());
                Long id = JWTUtil.getUserId(each);
                if (id == null) {
                    continue;
                }
                if (userId.equals(id)) {
                    //只要找到一个就行,且是不包含前缀的
                    userToken = each;
                    break;
                }
            }
        } catch (Exception e) {
            logErrorForBusiness("getOneTokenByUserId", e);
            throw e;
        } finally {
            closeJedisWithLogging("getOneTokenByUserId", jedis);
        }
        return userToken;
    }

    public static Set<String> getStringValues(String patternKey) {
        log.info("Redis execute [SCAN] Command");
        Set<String> result = new HashSet<>();
        try (Jedis jedis = jedisPoolInstance.getResource()) {
            // 游标初始值为0
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanParams scanParams = new ScanParams();
            scanParams.match(patternKey);
            //一次取10000个数据
            scanParams.count(10000);

            do {
                //使用scan命令获取数据，使用cursor游标记录位置，下次循环使用
                log.info("执行SCAN开始");
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                List<String> list = scanResult.getResult();
                log.info("执行SCAN结束");
                result.addAll(list);
                //返回0 说明遍历完成
                cursor = scanResult.getCursor();
            } while (!"0".equals(cursor));
        }
        return result;
    }

    public static Object setLock(String key, String owner, String expire) {
        try (Jedis jedis = jedisPoolInstance.getResource()) {
            return jedis.evalsha("f27503ceb0710511c666cb4e5f5ba4add7fdfcdd", 1, key, owner, expire);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {tag}
     *
     * @author shengling.guan
     */
    public static void releaseLock(String key, String owner) {
        try (Jedis jedis = jedisPoolInstance.getResource()) {
            jedis.evalsha("269c48ceace66208ec0a3fdbc99ec2232639267b", 1, key, owner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 ; false 不存在
     */
    public static boolean exists(String key) {
        Jedis jedis = jedisPoolInstance.getResource();
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("异常", e);
            return false;
        } finally {
            closeJedisWithLogging("exists", jedis);
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public static void incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        Jedis jedis = jedisPoolInstance.getResource();
        try {
            long value = Long.parseLong(jedis.get(key));
            value = value + delta;
            jedis.set(key, String.valueOf(value));
        } catch (Exception e) {
            log.error("异常", e);
        } finally {
            closeJedisWithLogging("incr", jedis);
        }
    }

    /**
     * 普通缓存存放
     *
     * @param key   键
     * @param value 值
     * @return true 成功 ；false 失败
     */
    public static boolean set(String key, Object value, Integer seconds) {
        Jedis jedis = jedisPoolInstance.getResource();
        try {
            if (seconds == null) {
                //没传过期时间，则不设置过期时间
                jedis.set(key, String.valueOf(value));
            } else {
                jedis.setex(key, seconds, String.valueOf(value));
            }
            return true;
        } catch (Exception e) {
            log.error("异常", e);
            return false;
        } finally {
            closeJedisWithLogging("set", jedis);
        }
    }

    /**
     * 字符串 根据key获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        Jedis jedis = jedisPoolInstance.getResource();
        try {
            return key == null ? null : jedis.get(key);
        } catch (Exception e) {
            log.error("异常", e);
            return null;
        } finally {
            closeJedisWithLogging("get", jedis);
        }
    }

}