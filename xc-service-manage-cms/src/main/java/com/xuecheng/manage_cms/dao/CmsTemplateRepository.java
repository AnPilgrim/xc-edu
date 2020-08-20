package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/11 14:03
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {

}
