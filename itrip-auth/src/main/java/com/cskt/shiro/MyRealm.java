package com.cskt.shiro;

import com.cskt.common.constants.ErrorCodeEnum;

import com.cskt.common.exception.ServiceException;
import com.cskt.entity.ItripUser;
import com.cskt.service.ItripUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author mo
 * @Description: 自定义认证及鉴权处理器
 * @date 2020-09-22 15:13
 */
//shiro中自定义realm一般继承AuthorizingRealm，
// 然后实现getAuthenticationInfo和getAuthorizationInfo方法，来完成登录和权限的验证。
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private ItripUserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 此期没有涉及到用户的角色，所只不需要实现鉴权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * 认证的方法：默认使用此方法进行用户正确与否验证，错误抛出异常即可
     * 1、判断token是否存在,如果不存在则抛出自定义异常。
     * 2、解析jwt格式的token,获取token中的头部信息的用户名称。判断是否存在这个用户名。
     * 3、如果存在用户名，再连接数据库查询，是否存在这个用户名的用户。
     * 4、如果存在这个用户，在根据用户名和密码还有jwt格式的token,在调用工具类对用户名、密码和当前获得的token进行比对，验证token是否正确。
     * 5、如果以上所有判断都通过才能通过此次认证。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        if (token == null) {
            throw new ServiceException(ErrorCodeEnum.AUTH_TOKEN_IS_EMPTY);
        }
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtil.getUsername(token);
        if (username == null) {
            throw new ServiceException(ErrorCodeEnum.AUTH_UNKNOWN);
        }
        //根据name进行一次查询
         ItripUser user = userService.findUserByUserCode(username);
        if (user == null) {
            throw new ServiceException(ErrorCodeEnum.AUTH_AUTHENTICATION_FAILED);
        }

        if (!JWTUtil.verify(token, username, user.getUserPassword())) {
            throw new ServiceException(ErrorCodeEnum.AUTH_AUTHENTICATION_FAILED);
        }
        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }

}
