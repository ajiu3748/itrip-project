package com.cskt.service;

import com.cskt.vo.SmsMessageVO;

/**
 * @author mo
 * @Description: 容联云短信service层接口
 * @date 2020-09-21 15:57
 */

public interface SmsService {

    /**
     * 用于发送短信
     * @param messageVO 短信发送对象
     */
    void sendMsg(SmsMessageVO messageVO);

}
