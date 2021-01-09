package com.cskt.service.impl;

import com.cskt.service.IMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cskt.entity.ItripUser;
import com.cskt.mapper.ItripUserMapper;
import com.cskt.service.ItripUserService;

@Service
public class ItripUserServiceImpl extends ServiceImpl<ItripUserMapper, ItripUser> implements ItripUserService{
    @Resource
    private ItripUserMapper userMapper;
    private static final Logger log =
            LoggerFactory.getLogger(ItripUserServiceImpl.class);
    String activeCodeKeyPre = "active:";
    @Value(value = "${email.send.enable}")
    private boolean enableSendEmail;
    @Resource
    private IMailService mailService;


}
