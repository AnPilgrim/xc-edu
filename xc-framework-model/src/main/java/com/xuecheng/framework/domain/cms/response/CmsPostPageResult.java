package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/2 12:03
 */
@Data
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {
     String pageUrl;
    public CmsPostPageResult(ResultCode resultCode,String pageUrl){
        super(resultCode);
        this.pageUrl = pageUrl;
    }
}
