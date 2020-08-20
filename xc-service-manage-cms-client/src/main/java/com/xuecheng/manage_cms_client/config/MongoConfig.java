package com.xuecheng.manage_cms_client.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/11 9:33
 */
@Configuration
public class MongoConfig {
    @Value("${spring.data.mongodb.database}")
    String db;

    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoClient){
        MongoDatabase mongoDatabase = mongoClient.getDatabase(db);
        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabase);
        return gridFSBucket;
    }
}
