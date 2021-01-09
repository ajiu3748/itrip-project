package com.cskt.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mo
 * @Description: 短信模板实体类
 * @date 2020-10-14 14:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsMessageVO {
    /**发送手机号*/
    private String phoneNum;
    /**短信中的前缀*/
    private String signName = "爱旅行";
    /**短信平台模板编码*/
    private String templateCod;
    /**短信内容，主要为动态条件的json格式字符串*/
    private String text;

    public SmsMessageVO(String phoneNum, String text) {
        this.phoneNum = phoneNum;
        this.templateCod="SMS_209190969";
        this.text = text;
    }



}
