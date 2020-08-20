package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class PageService {
    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsConfigRepository cmsConfigRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    /**
     * 页面查询方法
     * @param page 页码，从1开始计数
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page,int size, QueryPageRequest queryPageRequest){
        if(queryPageRequest == null){
            queryPageRequest = new QueryPageRequest();
        }
        //cmspage
        CmsPage cmsPage = new CmsPage();
        //从查询条件中获取放入cmspage对象中
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){cmsPage.setSiteId(queryPageRequest.getSiteId());}
        if(StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){cmsPage.setTemplateId(queryPageRequest.getTemplateId());}
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){cmsPage.setPageAliase(queryPageRequest.getPageAliase());}
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //Example
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        if(page <= 0){
            page = 1;
        }
        page = page - 1;
        if(size <= 0){
            size = 10;
        }
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        System.out.println(all);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }
    //新增页面
    public CmsPageResult add(CmsPage cmsPage){
        //校验页面名称、站点id、页面webpath的唯一性
        //查询到则添加失败，否则添加新页面
        CmsPage cmsPage1 = cmsPageRepository.findBySiteIdAndPageWebPathAndPageName(cmsPage.getSiteId(), cmsPage.getPageWebPath(), cmsPage.getPageName());
        if(cmsPage1 != null){
            //页面已经存在，抛异常
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
            //调用dao新增
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }
    //根据页面Id查询页面
    public CmsPage findById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }
    //修改页面
    public CmsPageResult update(String id,CmsPage cmsPage){
        CmsPage cmsPage1 = this.findById(id);
        if(cmsPage1!=null){
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            cmsPage1.setSiteId(cmsPage.getSiteId());
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
            cmsPage1.setPageName(cmsPage.getPageName());
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            cmsPage1.setDataUrl(cmsPage.getDataUrl());
            cmsPageRepository.save(cmsPage1);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage1);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //根据id删除页面
    public ResponseResult delete(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
    //根据id查询cmsConfig
    public CmsConfig getCOnfigById(String id){
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if(optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }
    //页面静态化方法
    /**
     * 静态化程序获取页面的DataUrl
     *
     * 静态化程序远程请求DataUrl获取数据模型
     *
     * 静态化程序获取页面模板信息
     *
     * 执行页面静态化
     */
    public String getPageHtml(String pageId){
        //获取数据模型
        Map model = getModelByPageId(pageId);
        if(model == null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模板
        String template = getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(template)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String content = generateHtml(template, model);
        if(StringUtils.isEmpty(content)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return content;
    }
    //获取数据模型
    private Map getModelByPageId(String pageId){
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
            //页面dataurl为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }
    //获取页面模板
    public String getTemplateByPageId(String pageId){
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取页面模板id
        String templateId = cmsPage.getTemplateId();
        if(StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if(optional.isPresent()){
            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //根据id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResources对象，获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            //从流中取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    //执行静态化
    public String generateHtml(String templateContent,Map model){
        //创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        //向configuration配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板内容
        try {
            Template template = configuration.getTemplate("template");
            //调用api进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //页面发布
    public ResponseResult post(String pageId){
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //存储文件到gridfs中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //发送消息给mq
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //保存html页面到GridFs中
    private CmsPage saveHtml(String pageId,String htmlContent){
        //先得到页面信息
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        ObjectId id = null;
        try {
            //将html文件转换成输入流
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件保存到GridFs中
            id = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将html文件id更新到cmsPage中
        cmsPage.setHtmlFileId(id.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }
    //发送消息给mq
    private void sendPostPage(String pageId){
        //先得到页面信息
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //拼接消息对象
        Map<String,String> msg = new HashMap();
        msg.put("pageId",pageId);
        //转成json串
        String jsonString = JSON.toJSONString(msg);
        String siteId = cmsPage.getSiteId();
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);
    }

    public CmsPageResult save(CmsPage cmsPage) {
        //判断页面是否存在
        CmsPage one = cmsPageRepository.findBySiteIdAndPageWebPathAndPageName(cmsPage.getSiteId(), cmsPage.getPageWebPath(), cmsPage.getPageName());
        if(one!=null){
            //更新
            return this.update(one.getPageId(),cmsPage);
        }
        return this.add(cmsPage);
    }

    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //保存到cms_page中
        CmsPageResult cmsPageResult = this.save(cmsPage);
        if(!cmsPageResult.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage page = cmsPageResult.getCmsPage();
        String pageId = page.getPageId();
        //页面发布
        ResponseResult post = this.post(pageId);
        if(!post.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //拼装URL
        String siteId = page.getSiteId();
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        if(cmsSite == null){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        String pageUrl = cmsSite.getSiteDomain()+cmsSite.getSiteWebPath()+page.getPageWebPath()+page.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);

    }
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
}
