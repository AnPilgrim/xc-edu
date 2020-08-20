package com.xuecheng.manage_cms.dao;

import com.xuecheng.manage_cms.service.PageService;
import org.hibernate.annotations.CollectionId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/11 14:25
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {
    @Autowired
    PageService pageService;
    @Test
    public void testGetPageHtml(){
        String pageHtml = pageService.getPageHtml("5abefd525b05aa293098fca6");
        System.out.println(pageHtml);
    }
}
