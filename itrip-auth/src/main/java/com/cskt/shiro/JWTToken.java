package com.cskt.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author mo
 * @Description:
 * @date 2020-09-22 15:12
 * jwt中用于保存相关用户信息的功能
 */
public class JWTToken implements AuthenticationToken {

    private static final long serialVersionUID = -2491813231213757097L;
    // 秘钥
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }
    @Override
    public Object getPrincipal() {
        return token;
    }


    @Override
    public Object getCredentials() {
        return token;
    }

}
