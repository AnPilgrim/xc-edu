package com.xuecheng.api.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/6 19:09
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;
    @Test
    public void searchAll() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索方式--搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //像搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse search = client.search(searchRequest);
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] hits1 = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:hits1){
            String id = hit.getId();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String  studymodel = (String)sourceAsMap.get("studymodel");
            Double  price = (Double)sourceAsMap.get("price");
            System.out.println(name);
        }
    }
}
