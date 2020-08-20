package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/16 10:44
 */
@Api(value = "数据字典接口",description = "提供数据字典接口的管理、查询功能")
public interface SysDicthinaryControllerApi {
    @ApiOperation("数据字典接口查询")
    public SysDictionary getByType(String type);
}
