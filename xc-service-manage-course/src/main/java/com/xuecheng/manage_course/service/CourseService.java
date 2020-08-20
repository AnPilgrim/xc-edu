package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/15 16:47
 */
@Service
public class CourseService {
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    TeachPlanMapper teachPlanMapper;
    @Autowired
    TeachPlanRepository teachPlanRepository;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    CourseMarketRepository courseMarketRepository;
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    CoursePubRepository coursePubRepository;
    @Autowired
    TeachPlanMediaRepository teachPlanMediaRepository;
    @Autowired
    TeachPlanMediaPubRepository teachPlanMediaPubRepository;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;


    public TeachplanNode findTeachPlanList(String courseId){
        return teachPlanMapper.selectList(courseId);
    }
    //添加课程计划
    @Transactional
    public ResponseResult addTeachPlan(Teachplan teachplan) {
        if(teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String courseid = teachplan.getCourseid();
        //parentId
        String parentid = teachplan.getParentid();
        if(StringUtils.isEmpty(parentid)){
            parentid = this.getTeachPlanRoot(courseid);
        }
        Optional<Teachplan> optional = teachPlanRepository.findById(parentid);
        Teachplan parentNode = optional.get();
        String grade = parentNode.getGrade();
        Teachplan teachplan1 = new Teachplan();
        BeanUtils.copyProperties(teachplan,teachplan1);
        teachplan1.setParentid(parentid);
        teachplan1.setCourseid(courseid);
        if(grade.equals("1")) {
            teachplan1.setGrade("2");
        }else {
            teachplan1.setGrade("3");
        }
        teachPlanRepository.save(teachplan1);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //查询课程根节点，如果查询不到则自动添加
    public String getTeachPlanRoot(String courseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            return null;
        }
        //拿到课程信息
        CourseBase courseBase = optional.get();
        List<Teachplan> teachplanList = teachPlanRepository.findByCourseidAndParentid(courseId, "0");
        if(teachplanList == null || teachplanList.size()<=0){
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            teachPlanRepository.save(teachplan);
            return teachplan.getId();
        }
        return teachplanList.get(0).getId();
    }
    //分页查询课程
    public QueryResponseResult<CourseInfo> findCourseList(String company_id, int page, int size, CourseListRequest courseListRequest){

        if(courseListRequest == null){
            courseListRequest = new CourseListRequest();
        }
        if(page<=0){
            page = 0;
        }
        if(size<=0){
            size = 20;
        }
        courseListRequest.setCompanyId(company_id);
        PageHelper.startPage(page,size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> result = courseListPage.getResult();
        long total = courseListPage.getTotal();
        QueryResult<CourseInfo> courseInfoQueryResult = new QueryResult<CourseInfo>();
        courseInfoQueryResult.setList(result);
        courseInfoQueryResult.setTotal(total);
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS,courseInfoQueryResult);
    }
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }

    public CourseBase findCourseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
    @Transactional
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if(optional.isPresent()){
            CourseBase courseBase1 = optional.get();
            BeanUtils.copyProperties(courseBase,courseBase1);
            courseBaseRepository.save(courseBase1);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket market = this.getCourseMarketById(id);
        if(market == null){
            courseMarketRepository.save(courseMarket);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        BeanUtils.copyProperties(courseMarket,market);
        courseMarketRepository.save(market);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        CoursePic coursePic = null;
        //先查询有没有该课程图片
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if(optional.isPresent()){
            coursePic = optional.get();
        }
        if(coursePic == null){
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if(optional.isPresent()){
            CoursePic coursePic = optional.get();
            return coursePic;
        }
        return null;
    }
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long result = coursePicRepository.deleteByCourseid(courseId);
        if(result > 0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
    //查询课程视图，包括基本信息、图片、营销、课程计划
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();
        //查询课程基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if(courseBaseOptional.isPresent()){
            CourseBase courseBase = courseBaseOptional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程图片
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
        if(coursePicOptional.isPresent()){
            CoursePic coursePic = coursePicOptional.get();
            courseView.setCoursePic(coursePic);
        }
        //查询课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if(courseMarketOptional.isPresent()){
            CourseMarket courseMarket = courseMarketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        //查询课程计划
        TeachplanNode teachplanNode = teachPlanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    public CourseBase findCourseBaseById(String courseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }

    public CoursePublishResult preview(String id) {
        CourseBase courseBaseById = this.findCourseBaseById(id);
        //请求cms添加页面
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBaseById.getName());
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setTemplateId(publish_templateId);
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if(!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();

        //远程调用
        //拼装预览的url
        String url = previewUrl+pageId;

        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }

    /**
     * 课程发布
     * @param id
     * @return
     */
    @Transactional
    public CoursePublishResult publish(String id) {
        //准备页面信息
        CourseBase courseBaseById = this.findCourseBaseById(id);
        //请求cms添加页面
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBaseById.getName());
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setTemplateId(publish_templateId);
        //调用cms一键发布接口将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if(!cmsPostPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //保存课程索引信息
        //先创建一个coursePub对象
        CoursePub coursePub = createCoursePub(id);
        //将coursePub保存到数据库
        saveCoursePub(id,coursePub);
        //缓存课程信息。。。

        //保存课程状态为--已发布
        CourseBase courseBase = this.saveCoursePubState(id);
        if(courseBase == null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        String pageUrl = cmsPostPageResult.getPageUrl();
        //向teachplanMediaPub中保存课程媒资信息
        saveTeachplanMediaPub(id);
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    //向teachplanMediaPub中保存课程媒资信息
    private void saveTeachplanMediaPub(String courseId){
        //先删除teachplanMediaPub中的记录
        teachPlanMediaPubRepository.deleteByCourseId(courseId);
        //从teachPlanMedia中查询
        List<TeachplanMedia> teachplanMediaList = teachPlanMediaRepository.findByCourseId(courseId);
        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        //将teachplanMediaList中的数据插入teachplanMediaPubs中
        for(TeachplanMedia teachplanMedia:teachplanMediaList){
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubs.add(teachplanMediaPub);
        }
        //将list插入teachplanMediaPub表中
        teachPlanMediaPubRepository.saveAll(teachplanMediaPubs);
    }

    //将coursePub保存到数据库
    private CoursePub saveCoursePub(String id,CoursePub coursePub){
        CoursePub coursePub1 = null;
        //根据id查询coursePub
        Optional<CoursePub> optional = coursePubRepository.findById(id);
        if(optional.isPresent()){
            coursePub1 = optional.get();
        }else{
            coursePub1 = new CoursePub();
        }
        BeanUtils.copyProperties(coursePub,coursePub1);
        coursePub1.setId(id);
        //时间戳
        coursePub1.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePub1.setPubTime(date);
        coursePubRepository.save(coursePub1);
        return coursePub1;
    }
    //先创建一个coursePub对象
    private CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //将课程基本信息属性拷贝到coursePub对象中
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(id);
        if(optionalCourseBase.isPresent()){
            CourseBase courseBase = optionalCourseBase.get();
            BeanUtils.copyProperties(courseBase,coursePub);
        }
        //将课程图片信息属性拷贝到coursePub对象中
        Optional<CoursePic> optionalCoursePic = coursePicRepository.findById(id);
        if(optionalCoursePic.isPresent()){
            CoursePic coursePic = optionalCoursePic.get();
            BeanUtils.copyProperties(coursePic,coursePub);
        }
        //将课程营销信息属性拷贝到coursePub对象中
        Optional<CourseMarket> optionalCourseMarket = courseMarketRepository.findById(id);
        if(optionalCourseMarket.isPresent()){
            CourseMarket courseMarket = optionalCourseMarket.get();
            BeanUtils.copyProperties(courseMarket,coursePub);
        }
        //将课程计划信息属性拷贝到coursePub对象中
        //json串保存到对象中
        TeachplanNode teachplanNode = teachPlanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(jsonString);
        return coursePub;
    }


    //更改课程状态方法--202002
    private CourseBase saveCoursePubState(String courseId){
        CourseBase courseBaseById = this.findCourseBaseById(courseId);
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }
    //保存课程计划与媒资的关联信息
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        if(teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //检验课程计划是否为第三级
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> optional = teachPlanRepository.findById(teachplanId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan teachplan = optional.get();
        String grade = teachplan.getGrade();
        if(StringUtils.isEmpty(grade) || !grade.equals("3")){
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        //查询teachplanmedia
        Optional<TeachplanMedia> optionalTeachplanMedia = teachPlanMediaRepository.findById(teachplanId);
        TeachplanMedia one =null;
        if(optionalTeachplanMedia.isPresent()){
            one = optionalTeachplanMedia.get();
        }
        else {
            one = new TeachplanMedia();
        }
        one.setCourseId(teachplan.getCourseid());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setTeachplanId(teachplanId);
        teachPlanMediaRepository.save(one);

        return new ResponseResult(CommonCode.SUCCESS);
    }
}
