package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/17 20:32
 */
@Api(value = "用户中心", description = "用户中心管理")
public interface UcenterControllerApi {
    @ApiOperation("根据用户账户查询用户信息")
    public XcUserExt getUserExt(String username);
}
