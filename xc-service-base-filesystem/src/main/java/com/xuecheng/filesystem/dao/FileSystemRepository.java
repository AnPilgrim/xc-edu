package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/20 9:40
 */
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
