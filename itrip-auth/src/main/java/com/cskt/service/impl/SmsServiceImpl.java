package com.cskt.service.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.cskt.common.constants.ErrorCodeEnum;
import com.cskt.common.exception.ServiceException;
import com.cskt.service.SmsService;
import com.cskt.vo.SmsMessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author mo
 * @Description:发送验证码
 * @date 2020-09-21 15:58
 */
@Service
public class SmsServiceImpl implements SmsService {
    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    /**
     *发送短信
     * @param messageVO 短信发送对象
     */
    @Override
    public void sendMsg(SmsMessageVO messageVO) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                //这里我们直接写死用户的accessKeyId和secret
                "LTAI4GFCZPt7GmxjrssrKY5E",
                "9fKVoeMaOzunLJHwxddAAgnFCiwbAh");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        //指定发送的号码，也就是用户注册的号码
        request.putQueryParameter("PhoneNumbers", messageVO.getPhoneNum());
        //指定短信中的前缀
        request.putQueryParameter("SignName", messageVO.getSignName());
        //需要发送的内容，json格式
        request.putQueryParameter("TemplateParam", messageVO.getText());
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("响应数据：{}",response.getData());
        } catch (ClientException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCodeEnum.ERROR_CALLING_THIRD_PARTY_SERVICE);
        }


    }
}
