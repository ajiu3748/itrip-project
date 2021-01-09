package com.cskt.service.impl;

import com.cskt.service.IItripEmailService;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description:邮箱service层
 * @author: Mr.阿九
 * @createTime: 2021-01-07 16:43
 **/
@Service
public class ItripEmailServiceImpl implements IItripEmailService {
    @Resource
    private MailSender mailSender;

    /**
     * 发送邮箱
     * @param mailTo 收件人的邮箱地址
     * @param message 邮件内容
     */
    @Override
    public void sendActivationMail(String mailTo, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(mailTo);
        simpleMailMessage.setText(message);
        mailSender.send(simpleMailMessage);
    }

}
