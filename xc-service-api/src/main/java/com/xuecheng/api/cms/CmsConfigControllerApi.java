package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/10 14:46
 */
@Api(value = "cms配置管理接口",description = "cms配置管理接口，提供数据模型的管理、查询接口")
public interface CmsConfigControllerApi {
    @ApiOperation("根据Id查询CMS配置信息")
    public CmsConfig getmodel(String id);
}
