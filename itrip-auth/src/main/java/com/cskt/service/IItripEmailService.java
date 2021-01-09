package com.cskt.service;

public interface IItripEmailService {
    /**
     * 发送包括激活码的邮件，用于激活用户账号
     * @param mailTo 收件人的邮箱地址
     * @param message 邮件内容
     */
    void sendActivationMail(String mailTo, String message);

}
