package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/30 15:59
 */
@FeignClient(value = "XC-SERVICE-MANAGE-CMS")//指定要远程调用的服务名
public interface CmsPageClient {
    @GetMapping("/cms/page/get/{id}")//标识远程调用的http方法
    public CmsPage findCmsPageById(@PathVariable("id") String id);

    //添加页面，用于页面预览
    @PostMapping("/cms/page/save")
    public CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage);

    //一键发布
    @PostMapping("/cms/page/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);
}
