package com.cskt.itripauth.controller;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cskt.common.constants.ErrorCodeEnum;
import com.cskt.common.vo.ReturnResult;
import com.cskt.entity.ItripUser;
import com.cskt.service.IItripEmailService;
import com.cskt.service.IItripUserService;
import com.cskt.service.ItripUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.regex.Pattern;

/**
 * @description: 用户控制器
 * @author: Mr.阿九
 * @createTime: 2021-01-05 15:44
 **/
//Api：用在controller上，而ApiModal：用在实体类上
@Api("用户控制器")
@RestController
@RequestMapping(value = "/api/")
public class UserController {

    @Autowired
    private IItripUserService itripUserService;

    /**
     * 判断用户是否已存在
     * @param userCode
     * @return
     */
    //ApiOperation：文档中方法的作用
    @ApiOperation("验证邮箱或手机号是否存在")
    @GetMapping(value = "checkUserMail")
    public ReturnResult checkUserMail(String userCode) {
        if (StringUtils.isEmpty(userCode)) {
            //参数为空，则抛出异常
            return ReturnResult.error(ErrorCodeEnum.AUTH_PARAMETER_IS_EMPTY);
        }
        QueryWrapper<ItripUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userCode);
        ItripUser user = itripUserService.getOne(queryWrapper);
        if (user != null) {
            //当用户数据不为空的时候，校验不通过-》用户已存在
            if(user.getActivated()==1){
                //如果是激活状态返回已存在
                return ReturnResult.error(ErrorCodeEnum.AUTH_USER_ALREADY_EXISTS);
            }else{
                //未激活前端提示发送链接
                return ReturnResult.error(ErrorCodeEnum.AUTH_NOT_ACTIVATE);
            }
        }
        return ReturnResult.ok();
    }

    /**
     * 邮箱注册
     * @param user
     * @return
     */
    @ApiOperation("邮箱注册")
    @PostMapping(value = "doRegisterMail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReturnResult doRegisterMail(@ApiParam(name = "user",value = "用户实体类",required=true) @RequestBody ItripUser user) {
        //校验邮箱地址是否符合要求
        if (!validEmail(user.getUserCode())) {
            return ReturnResult.error(ErrorCodeEnum.AUTH_ILLEGAL_USERCODE);
        }
        boolean result = itripUserService.userRegisterMail(user);
        if (result) {
            return ReturnResult.ok();
        }
        return ReturnResult.error();
    }

    /**
     * 通过正则表达式校验邮箱地址是否符合要求
     * 合法E-mail地址：
     * 1. 必须包含一个并且只有一个符号“@”
     * 2. 第一个字符不得是“@”或者“.”
     * 3. 不允许出现“@.”或者.@
     * 4. 结尾不得是字符“@”或者“.”
     * 5. 允许“@”前的字符中出现“＋”
     * 6. 不允许“＋”在最前面，或者“＋@”
     * @param email
     * @return
     */
    private boolean validEmail(String email){
        String regex="^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$" ;
        return Pattern.compile(regex).matcher(email).find();
    }


    /**
     * 邮箱激活方法
     * @param code
     * @return
     */
    @ApiOperation("邮箱激活")
    @GetMapping(value = "/activateMail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReturnResult activateMail(@RequestParam String code) {
            //判断参数是否为空
        if (StringUtils.isEmpty(code)) {
            return ReturnResult.error(ErrorCodeEnum.AUTH_PARAMETER_IS_EMPTY);
        }
        boolean activeResult = itripUserService.activeEmail(code);
        if (activeResult) {
            return ReturnResult.ok();
        } else {
            return ReturnResult.error();
        }
    }

    /**
     * 邮箱再次激活方法
     * @param code
     * @return
     */
    @ApiOperation("邮箱再次激活")
    @GetMapping(value = "/reactiveEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReturnResult reactiveEmailMail(@RequestParam String code){
        Boolean bo=itripUserService.sendEmail(code);
        if (bo) {
            return ReturnResult.ok();
        } else {
            return ReturnResult.error();
        }
    }


    /**
     * 手机注册
     * @param user
     * @return
     */
    @ApiOperation("手机注册")
    @PostMapping(value = "doRegisterSms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReturnResult doRegisterSms(@ApiParam(name = "user",value = "用户实体类",required=true)
                                          @RequestBody ItripUser user ,String validation) {
        //校验手机号是否符合要求
        if (!validSms(user.getUserCode())) {
            return ReturnResult.error(ErrorCodeEnum.AUTH_ILLEGAL_USERCODE);
        }
        //新增
        boolean result = itripUserService.userRegisterSms(user,validation);
        if (result) {
            return ReturnResult.ok();
        }
        return ReturnResult.error();
    }


    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @ApiOperation("获取验证码")
    @GetMapping(value = "/getRegister", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReturnResult getRegister(@RequestParam String phone) throws JsonProcessingException {
        Boolean bo=itripUserService.sendSms(phone);
        if (bo) {
            return ReturnResult.ok();
        } else {
            return ReturnResult.error();
        }
    }

    /**
     * 通过正则表达式校验手机号是否符合要求
     * @param phone
     * @return
     */
    private boolean validSms(String phone){
        String regex="^1[34578]{1}\\\\d{9}$" ;
        return Pattern.compile(regex).matcher(phone).find();
    }

}
