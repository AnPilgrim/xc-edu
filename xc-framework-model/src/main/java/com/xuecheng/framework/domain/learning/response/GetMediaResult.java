package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/15 13:59
 */
@Data
@NoArgsConstructor
public class GetMediaResult extends ResponseResult {
    //视频播放地址
    String fileUrl;
    public GetMediaResult(ResultCode resultCode, String fileUrl){
        super(resultCode);
        this.fileUrl = fileUrl;
    }
}
