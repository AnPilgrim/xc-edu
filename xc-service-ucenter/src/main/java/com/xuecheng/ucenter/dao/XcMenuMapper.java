package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/19 10:44
 */
@Mapper
public interface XcMenuMapper {
    //根据id查询用户权限
    public List<XcMenu> selectPermissionByUserId(String userid);
}
