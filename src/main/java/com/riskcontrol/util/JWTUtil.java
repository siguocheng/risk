package com.riskcontrol.util;

import com.riskcontrol.domain.vo.TokenUserBean;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JWTUtil {
    /**
     * 一开始的想法是通过JWT给token本身设置过期时间，但是因为有需求，就是如果用户持续在使用系统，那么就不能因为token到期，迫使用户退出系统，
     * 需要通过redis给token延期
     */
    public static final int EXPIRE_TIME = 60 * 60 * 36;
    public static final int EXPIRE_TTL_TIME = 60 * 5; // redis中token过期的剩余时间

    public static final int EXPIRE_TIME_30 = 60 * 60 * 24 * 30;
    public static final int EXPIRE_TIME_15 = 60 * 15;
    // 密钥
    private static final String SECRET = "abcd999efghabcdefghabcdefghabcdefgh";

    private static final String userId= "userId";
    private static final String username = "username";
    private static final String userlevel = "userlevel";

    private static final String TIMESTAMP = "timestamp";


    // 获取加密密钥
    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 token
     * 通过userId,username,订阅号生成一个token,但是不需要给token配置过期时间，因为现在的逻辑是，在把token放入到redis中的时候设置过期时间，
     * 并且，如果用户一直在调用接口，然后拦截器判断token在redis的过期时间是否小于一定时间，例如30分钟，符合条件那就给token延期
     * @return 加密的token
     */
    public static String createToken(TokenUserBean tokenUserBean) {
        return Jwts.builder()
                .claim(userId, tokenUserBean.getUserId())
                .claim(username, tokenUserBean.getUserName())
                .claim(TIMESTAMP, System.nanoTime())
                .signWith(getSecretKey(), SignatureAlgorithm.HS256).compact();
    }

    public static Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public static void main(String[] args) {
//        User user = new User();
//        user.setId(999L);
//        user.setName("WCS");
//        user.setLevel(0);
//        TokenUserBean tokenUser = TokenUserBean.generateTokenUserBean(user, new ArrayList<>(), false);
//
//        String token = JWTUtil.createPermanentToken(tokenUser);
//        System.out.println("永久token:" + token);
    }

    /**
     * 校验token是否有效
     */
    public boolean validateToken(String token){
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            // token过期
        } catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // 非法token、签名错误
        }
        return false;
    }


    /**
     * 校验 token 是否正确
     * @param token    密钥
     * @return 是否正确
     */
    public static boolean verify(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            // token过期
        } catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // 非法token、签名错误
        }
        return false;
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);

        return (String)claims.get(username);
    }

    public static Long getUserId(String token) {
        Claims claims = parseToken(token);

        Integer uId = (Integer)(claims.get(userId));

        return uId.longValue();
    }

}
