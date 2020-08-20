package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/14 17:13
 */
@Service
public class PageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    CmsSiteRepository cmsSiteRepository;
    //保存html页面到服务器路径
    public void savePageToServerPath(String pageId){
        //根据id查询cmspage
        CmsPage cmsPage = this.findCmsPageById(pageId);
        //0.得到html的文件id，从cmsPgae中获取htmlFieldId内容
        String htmlFileId = cmsPage.getHtmlFileId();
        //1.从GridFs中查html页面
        InputStream inputStream = this.getFileById(htmlFileId);
        if(inputStream == null){
            LOGGER.error("getFileById InputStream is null,htmlFileId:{}",htmlFileId);
            return;
        }
        //得到站点物理路径
        CmsSite cmsSite = this.findCmsSiteById(cmsPage.getSiteId());
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //得到页面物理路径
        String pagePath = sitePhysicalPath + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        //2.将html保存到服务器物理路径
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //根据文件id从GridFs中查文件内容
    public InputStream getFileById(String fileId){
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //根据页面id查询页面信息
    public CmsPage findCmsPageById(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
    //根据站点id查询站点信息
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

}
