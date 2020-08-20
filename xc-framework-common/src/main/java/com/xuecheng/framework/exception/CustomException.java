package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 自定义异常类型
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/9 14:21
 */
public class CustomException extends RuntimeException {
    ResultCode resultCode;
    public CustomException(ResultCode resultCode){
        this.resultCode = resultCode;
    }
    public ResultCode getResultCode(){return resultCode;}
}
