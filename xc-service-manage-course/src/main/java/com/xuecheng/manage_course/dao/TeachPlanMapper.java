package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/15 16:30
 */
@Mapper
public interface TeachPlanMapper {
    //课程计划查询
    public TeachplanNode selectList(String courseId);
}
