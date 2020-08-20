package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/1 8:44
 */
@Data
@ToString
@NoArgsConstructor
public class CoursePublishResult extends ResponseResult {
    String previewUrl;
    public CoursePublishResult(ResultCode resultCode,String previewUrl){
        super(resultCode);
        this.previewUrl = previewUrl;
    }
}
