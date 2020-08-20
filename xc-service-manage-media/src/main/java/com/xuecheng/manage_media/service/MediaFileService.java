package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/13 19:24
 */
@Service
public class MediaFileService {
    @Autowired
    MediaFileRepository mediaFileRepository;

    //查询媒资列表
    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        if(queryMediaFileRequest == null){
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //条件值对象
        MediaFile mediaFile = new MediaFile();
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tag",ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName",ExampleMatcher.GenericPropertyMatchers.contains());
        Example<MediaFile> example = Example.of(mediaFile,exampleMatcher);
        //分页查询对象
        if(page<=0){
            page = 1;
        }
        page = page - 1;
        if(size<=0){
            size = 10;
        }
        Pageable pageable = new PageRequest(page,size);
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        long totalElements = all.getTotalElements();
        List<MediaFile> content = all.getContent();

        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setList(content);
        queryResult.setTotal(totalElements);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;

    }
}
