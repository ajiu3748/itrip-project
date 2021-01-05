package com.cskt.common.exception;

import com.cskt.common.constants.ErrorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: service层异常类
 * @author: Mr.阿九
 * @createTime: 2021-01-05 11:14
 **/
@Data
@ApiModel(value = "自定义service层异常")
public class ServiceException extends RuntimeException{
    @ApiModelProperty(value = "异常状态码")
    private String errorCode;
    public ServiceException(String message, String errorCode) {
        super(message); this.errorCode = errorCode;
    }
    public ServiceException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg()); this.errorCode = errorCodeEnum.getErrorCode();
    }
}
