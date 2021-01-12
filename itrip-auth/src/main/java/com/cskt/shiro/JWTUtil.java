package com.cskt.shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author mo
 * @Description: Jwt工具类，用于生成和校验jwt
 * @date 2020-09-22 15:01
 */

public class JWTUtil {

    /**过期时间为2小时*/
    private static final long EXPRIE_TIME = 2*60*60*1000;

    /**
     * 校验用户名和密码
     * @param token
     * @param username
     * @param secret  加密后的用户密码
     * @return
     */
    public static boolean verify(String token, String username, String secret) {
        try {
            //加密获得签名
            Algorithm algorithm = Algorithm.HMAC512(secret);
            //根据签名获得jwt的信息对象
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取token中的信息无需secret解密也能获得
     * @param token
     * @return
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").toString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成JWTToken
     * @param username  用户名
     * @param secret 加密后的用户密码（MD5加密后的密码）
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String sign(String username, String secret) throws UnsupportedEncodingException {
        //生成token的失效时间
        Date date = new Date(System.currentTimeMillis()+EXPRIE_TIME);
        //生成token签名对象
        Algorithm algorithm = Algorithm.HMAC512(secret);
        // 附带username信息，创建jwt
        return JWT.create()
                .withClaim("username", username)
                .withExpiresAt(date)
                .sign(algorithm);
    }

}
