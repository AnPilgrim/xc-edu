package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest//找启动类启动
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Test
    public void testFindAll(){
        List<CmsPage> list = cmsPageRepository.findAll();
        System.out.println(list);
    }
    //分页查询
    @Test
    public void testFindPage(){
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }
    @Test
    public void findAllByExampleTest(){
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);
        //cmspage
        CmsPage cmsPage = new CmsPage();
        //cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        //设置页面别名（模糊匹配）
        cmsPage.setPageAliase("轮播");
        //条件匹配器(精确查询还是模糊查询)
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //Example
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> list = all.getContent();
        System.out.println(list);
    }
}
