package com.xuecheng.api.search;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/6 19:09
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;

    //删除索引库
    @Test
    public void testDeleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest =  new DeleteIndexRequest("xc_course");
        IndicesClient indices = client.indices();
        DeleteIndexResponse delete = indices.delete(deleteIndexRequest);
        boolean acknowledged = delete.isAcknowledged();
        //...
    }
    //创建索引库
    @Test
    public void testCreateIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));
        createIndexRequest.mapping("doc","//映射内容", XContentType.JSON);
        IndicesClient indices = client.indices();
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        //...
    }
}
