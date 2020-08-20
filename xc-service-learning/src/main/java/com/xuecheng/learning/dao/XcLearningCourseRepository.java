package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/20 10:01
 */
public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse,String> {
    //根据用户和课程id查询
    XcLearningCourse findByUserIdAndCourseId(String userId, String courseId);
}
