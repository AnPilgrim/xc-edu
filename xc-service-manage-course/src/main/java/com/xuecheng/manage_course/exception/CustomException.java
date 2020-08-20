package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**课程管理自定义异常类型，其中定义异常类型所对应的错误代码
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/19 10:25
 */
@ControllerAdvice
public class CustomException extends ExceptionCatch {
    static {
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }
}
