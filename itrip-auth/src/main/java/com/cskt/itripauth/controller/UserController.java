package com.cskt.itripauth.controller;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cskt.common.constants.ErrorCodeEnum;
import com.cskt.common.vo.ReturnResult;
import com.cskt.entity.ItripUser;
import com.cskt.service.ItripUserService;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: 用户控制器
 * @author: Mr.阿九
 * @createTime: 2021-01-05 15:44
 **/
@RestController
@RequestMapping(value = "/api")
public class UserController {

    @Resource
    private ItripUserService userService;

    /**
     * 判断用户是否已存在
     * @param name
     * @return
     */
    @GetMapping(value = "/isUser")
    public ReturnResult checkUser(String name) {
        if (StringUtils.isEmpty(name)) {
            //参数为空
            return ReturnResult.error(ErrorCodeEnum.AUTH_PARAMETER_IS_EMPTY);
        }
        QueryWrapper<ItripUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", name);
        ItripUser user = userService.getOne(queryWrapper);
        if (user != null) {
            //当用户数据不为空的时候，校验不通过
            return ReturnResult.error(ErrorCodeEnum.AUTH_USER_ALREADY_EXISTS);
        }
        return ReturnResult.ok();
    }

}
