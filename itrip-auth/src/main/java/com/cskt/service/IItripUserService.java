package com.cskt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cskt.entity.ItripUser;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface IItripUserService extends IService<ItripUser> {

    /**
     * 用户注册
     * @param user
     * @return
     */
    Boolean userRegisterMail(ItripUser user);
    Boolean sendEmail(String code);
    Boolean activeEmail(String code);
    Boolean userRegisterSms(ItripUser user,String code);
    Boolean sendSms(String code) throws JsonProcessingException;
}
