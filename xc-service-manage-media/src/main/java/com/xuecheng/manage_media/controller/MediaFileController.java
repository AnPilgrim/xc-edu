package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaFileControllerApi;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_media.service.MediaFileService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/14 14:32
 */
@RestController
@RequestMapping("/media/file")
public class MediaFileController implements MediaFileControllerApi {

    @Autowired
    MediaFileService mediaFileService;
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult<MediaFile> findList(@PathVariable("page") int page,@PathVariable("size") int size, QueryMediaFileRequest queryMediaFileRequest) {
        return mediaFileService.findList(page,size,queryMediaFileRequest);
    }
}
