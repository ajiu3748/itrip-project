package com.cskt.itripauth.controller;

import com.aliyuncs.utils.StringUtils;
import com.cskt.common.constants.ErrorCodeEnum;
import com.cskt.common.vo.ReturnResult;
import com.cskt.entity.ItripUser;
import com.cskt.service.ItripUserService;
import com.cskt.shiro.JWTUtil;
import com.cskt.util.JsonUtil;
import com.cskt.util.MD5;
import com.cskt.vo.TokenVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * @description: 登陆控制器
 * @author: Mr.阿九
 * @createTime: 2021-01-06 15:48
 **/
@Api(tags = "登录相关控制器")
@RestController
@RequestMapping(value = "/auth/api")
public class LoginController {
    @Resource
    private ItripUserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger log =
            LoggerFactory.getLogger(LoginController.class);

    /**
     * 1、判断用户名密码是否为空
     * 2、判断用户名是否已经注册
     * 3、判断密码是否正确
     * 4、根据用户名与经过MD5加密后的密码生成token
     * 5、存储到redis中，将token作为redis中key，用户信息作为值
     * 6、返回token的响应对象
     * @param userCode
     * @param userPassword
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @ApiOperation(value = "登录接口")
    @PostMapping(value = "/dologin", produces =MediaType.APPLICATION_JSON_VALUE)
    public ReturnResult login(@ApiParam(name = "name", value = "登录用户名")
                              @RequestParam("name") String userCode,
                              @ApiParam(name = "password", value = "登录密码")
                              @RequestParam("password") String userPassword,
                              HttpServletRequest request) throws JsonProcessingException {
        if (StringUtils.isEmpty(userCode) || StringUtils.isEmpty(userPassword))
        {
            //当用户编码或用户密码为空的时候
            return ReturnResult.error(ErrorCodeEnum.AUTH_PARAMETER_ERROR);
        }
        ItripUser user = userService.findUserByUserCode(userCode);
        if (user == null) {
            return ReturnResult.error(ErrorCodeEnum.AUTH_UNKNOWN);
        }
        if (!MD5.getMd5(userPassword, 32).equals(user.getUserPassword())) {
            //当密码不匹配时返回错误信息
            return ReturnResult.error(ErrorCodeEnum.AUTH_AUTHENTICATION_FAILED);
        }
        String jwtToken = null;
        try {
            jwtToken = JWTUtil.sign(userCode, MD5.getMd5(userPassword, 32));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        TokenVo tokenVo = new TokenVo(jwtToken, System.currentTimeMillis() + 2 *
                3600 * 1000, System.currentTimeMillis());
          //将用户信息存储到redis中，以jwtToken做为key，跟需求有一些出入，不区分移动端和PC端
        stringRedisTemplate.opsForValue().set(jwtToken,
                JsonUtil.objectToJsonString(user), 2, TimeUnit.HOURS);
        return ReturnResult.ok(tokenVo);
    }



}
