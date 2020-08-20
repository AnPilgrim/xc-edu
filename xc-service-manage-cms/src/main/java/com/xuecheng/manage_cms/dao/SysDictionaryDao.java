package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/16 10:46
 */
@Repository
public interface SysDictionaryDao extends MongoRepository<SysDictionary,String> {
    //根据字典分类查询字典信息
    SysDictionary findByDType(String dtype);
}
