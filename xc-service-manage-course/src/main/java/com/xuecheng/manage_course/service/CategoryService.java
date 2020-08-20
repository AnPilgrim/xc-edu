package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/16 10:19
 */
@Service
public class CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    public CategoryNode findList(){
        return categoryMapper.selectList();
    }
}
