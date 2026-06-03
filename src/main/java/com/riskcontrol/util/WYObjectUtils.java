package com.riskcontrol.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author fallrain
 *
 * ObjectUtils
 * 对象集合转换工具类
 * 对象工具类
 * @Description TODO
 * @Date 2021/7/19 15:32
 * @Version 1.0
 */
public class WYObjectUtils {

    /**
     * 对象或Map转Bean
     *
     * @param <T>    转换的Bean类型
     * @param source Bean对象或Map
     * @param clazz  目标的Bean类型
     * @return Bean对象
     * @since 4.1.20
     */
    public static <T> T convert(Object source, Class<T> clazz) {
        return BeanUtil.toBean(source, clazz);
    }


    /**
     * list转list
     * @param source
     * @param r
     * @param <T>
     * @param <R>
     * @return
     */
    public static  <T,R> List<R> convert(List<T> source, Class<R> r)  {
        List<R> list = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(source)) {
            for(int i=0; i<source.size(); i++) {
                T t = source.get(i);
                Object obj = null;
                try {
                    obj = r.getDeclaredConstructor().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                BeanUtils.copyProperties(t, obj);
                list.add((R)obj);
            }
        }
        return list;
    }

    public static boolean isNull(Object... o){
        if (o.length==0){
            return true;
        }
        for ( Object o1:o){
            if (o1==null){
                return true;
            }
        }
        return false;
    }


    /**
     * Object转BigDecimal类型
     *
     * @param value 要转的object类型
     * @return 转成的BigDecimal类型数据
     */
    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }


    /**
     * 将对象转换为Map，并格式化日期字段
     * @param object 要转换的对象
     * @return 包含对象属性的Map，日期字段已格式化为字符串
     */
    public static Map<String, Object> beanToMapWithDateFormat(Object object) {
        Map<String, Object> map = BeanUtil.beanToMap(object);
        // 处理各种日期字段类型
        map.entrySet().forEach(entry -> {
            Object value = entry.getValue();
            if (value instanceof LocalDate) {
                // 处理LocalDate类型
                entry.setValue(((LocalDate) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } else if (value instanceof java.util.Date) {
                // 处理Date类型
                entry.setValue(new SimpleDateFormat("yyyy-MM-dd").format((java.util.Date) value));
            } else if (value instanceof java.time.LocalDateTime) {
                // 处理LocalDateTime类型
                entry.setValue(((java.time.LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else if (value instanceof java.time.LocalTime) {
                // 处理LocalTime类型
                entry.setValue(((java.time.LocalTime) value).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            } else if (value instanceof BigDecimal) {
                // 处理BigDecimal类型，去除小数点后的无用0
                entry.setValue(BigDecimalCalculateUtil.formatNumber( WYObjectUtils.getBigDecimal(value)));
        }
        });
        return map;
    }

    /**
     * 将对象列表转换为Map列表，并对日期格式进行特殊处理
     *
     * @param dataList 待转换的对象列表，可以为null
     * @return 转换后的Map列表，如果输入为null则返回空列表
     */
    public static List<Map<String, Object>> convertListToMapListWithDateFormat(List<?> dataList) {
        // 使用Optional处理可能为null的输入列表，避免空指针异常
        // 对列表中的每个对象调用beanToMapWithDateFormat方法进行转换
        return Optional.ofNullable(dataList)
                .orElse(Collections.emptyList())
                .stream()
                .map(WYObjectUtils::beanToMapWithDateFormat)
                .collect(Collectors.toList());
    }

    public static boolean isNull(Object obj) {
        return ObjectUtil.isNull(obj);
    }

    public static boolean isNotNull(Object obj) {
        return ObjectUtil.isNotNull(obj);
    }


}



