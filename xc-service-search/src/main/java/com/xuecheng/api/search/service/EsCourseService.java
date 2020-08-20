package com.xuecheng.api.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/8 20:23
 */
@Service
public class EsCourseService {

    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.course.source_field}")
    private String source_field;
    @Value("${xuecheng.media.index}")
    private String media_index;
    @Value("${xuecheng.media.type}")
    private String media_type;
    @Value("${xuecheng.media.source_field}")
    private String media_source_field;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        if(courseSearchParam == null){  courseSearchParam = new CourseSearchParam();    }
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置搜索类型
        searchRequest.types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //过滤源字段
        String[] source_filed_array = source_field.split(",");
        searchSourceBuilder.fetchSource(source_filed_array,new String[]{});
        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //搜索条件
        //根据关键字搜索
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%")
                    .field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        //根据分类搜索
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())) {
            //根据一级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getSt())) {
            //根据二级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            //根据难度等级
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }

        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页参数
        if(page <= 0){
            page = 1;
        }
        if(size <= 0){
            size = 12;
        }
        int from = (page-1)*size;
        searchSourceBuilder.from(from);//起始记录下标
        searchSourceBuilder.size(size);
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        QueryResult<CoursePub> queryResult = new QueryResult<>();
        List<CoursePub> list = new ArrayList<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            //获取响应结果
            SearchHits hits = search.getHits();
            long totalHits = hits.getTotalHits();
            queryResult.setTotal(totalHits);
            SearchHit[] searchHits = hits.getHits();
            for(SearchHit searchHit:searchHits){
                CoursePub coursePub = new CoursePub();
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                String id = (String)sourceAsMap.get("id");
                coursePub.setId(id);
                String name = (String) sourceAsMap.get("name");
                //取出高亮字段
                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                if(highlightFields!=null){
                    HighlightField highlightFieldName = highlightFields.get("name");
                    if(highlightFieldName!=null) {
                        Text[] fragments = highlightFieldName.fragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for(Text text:fragments){
                            stringBuffer.append(text);
                        }
                        name = stringBuffer.toString();
                    }
                }
                coursePub.setName(name);
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                Double price = null;
                try {
                    if(sourceAsMap.get("price") != null){
                        price =(Double)sourceAsMap.get("price");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                //旧价格
                Double price_old = null;
                try {
                    if(sourceAsMap.get("price_old") != null){
                        price_old =(Double)sourceAsMap.get("price_old");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                list.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryResult.setList(list);
        return new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
    }
    //使用es客户端查询信息
    public Map<String, CoursePub> getall(String id) {
        //定义一个搜索对象
        SearchRequest searchRequest = new SearchRequest(index);
        //指定type
        searchRequest.types(type);
        //定义SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termQuery
        searchSourceBuilder.query(QueryBuilders.termQuery("id",id));
        //

        searchRequest.source(searchSourceBuilder);
        CoursePub coursePub = new CoursePub();
        Map<String,CoursePub> map = new HashMap<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] searchHits = hits.getHits();
            for(SearchHit hit:searchHits){
                //获取原文档内容
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String courseId = (String)sourceAsMap.get("id");
                String name = (String)sourceAsMap.get("name");
                String grade = (String)sourceAsMap.get("grade");
                String charge = (String)sourceAsMap.get("charge");
                String pic = (String)sourceAsMap.get("pic");
                String description = (String)sourceAsMap.get("description");
                String teachplan = (String)sourceAsMap.get("teachplan");
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setGrade(grade);
                coursePub.setPic(pic);
                coursePub.setCharge(charge);
                coursePub.setDescription(description);
                coursePub.setTeachplan(teachplan);
                map.put(courseId,coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    //根据多个课程计划id查询
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
        //定义一个搜索对象
        SearchRequest searchRequest = new SearchRequest(media_index);
        //指定type
        searchRequest.types(media_type);
        //定义SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termQuery
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        //过滤源字段
        String[] split = media_source_field.split(",");
        searchSourceBuilder.fetchSource(split,new String[]{});

        searchRequest.source(searchSourceBuilder);
        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        long total = 0;
        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            total = hits.getTotalHits();
            SearchHit[] searchHits = hits.getHits();
            for(SearchHit hit:searchHits){
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);

                teachplanMediaPubs.add(teachplanMediaPub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(teachplanMediaPubs);
        queryResult.setTotal(total);
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
