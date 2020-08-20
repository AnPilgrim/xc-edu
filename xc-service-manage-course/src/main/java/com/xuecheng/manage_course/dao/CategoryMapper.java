package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/16 10:04
 */
@Mapper
public interface CategoryMapper {
    //分类查询
    public CategoryNode selectList();
}
