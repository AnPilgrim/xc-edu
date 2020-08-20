package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/16 10:48
 */
@Service
public class SysdictionaryService {
    @Autowired
    SysDictionaryDao sysDictionaryDao;
    //根据字典分类type查询字典信息
    public SysDictionary findDictionaryByType(String type){
        return sysDictionaryDao.findByDType(type);
    }
}
