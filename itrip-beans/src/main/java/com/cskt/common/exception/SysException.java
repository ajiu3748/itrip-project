package com.cskt.common.exception;

import com.cskt.common.constants.ErrorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 系统异常类
 * @author: Mr.阿九
 * @createTime: 2021-01-05 11:16
 **/
@Data
@ApiModel(value = "自定义系统异常")
public class SysException extends RuntimeException{
    @ApiModelProperty(value = "异常编码")
    private String errorCode;
    public SysException(String message,String errorCode) { super(message); this.errorCode = errorCode; }
    public SysException(ErrorCodeEnum errorCodeEnum) { super(errorCodeEnum.getMsg()); this.errorCode = errorCodeEnum.getErrorCode(); }
}
