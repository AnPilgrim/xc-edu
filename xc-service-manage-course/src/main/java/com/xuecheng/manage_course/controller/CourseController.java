package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/15 16:47
 */
@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {
    @Autowired
    CourseService courseService;
    @PreAuthorize("hasAuthority('course_teachplan_list')")
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachPlanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachPlanList(courseId);
    }
    @PreAuthorize("hasAuthority('course_teachplan_add')")
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachPlan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachPlan(teachplan);
    }

    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        //要拿到当前用户所属Id
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        String company_id = userJwt.getCompanyId();
        return courseService.findCourseList(company_id,page,size,courseListRequest);
    }

    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    @Override
    @GetMapping("/manage/baseinfo/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) {
        return courseService.findCourseById(courseId);
    }

    @Override
    @PostMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBase(@PathVariable("id") String id,@RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(id,courseBase);
    }

    @Override
    @GetMapping("/manage/marketinfo/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    @Override
    @PostMapping("/coursebase/marketinfo/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id,@RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarket(id,courseMarket);
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId,@RequestParam("pic") String pic) {
        return courseService.addCoursePic(courseId,pic);
    }
    @PreAuthorize("hasAuthority('course_find_list')")
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePic(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseview(@PathVariable("id") String id) {
        return courseService.getCourseView(id);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable("id") String id) {
        return courseService.publish(id);
    }

    @Override
    @PostMapping("savemedia")
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemedia(teachplanMedia);
    }
}
