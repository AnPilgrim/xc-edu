package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/10 14:46
 */
@Api(value = "文件管理接口",description = "文件管理接口，提供数据模型的管理、查询接口")
public interface FileSystemControllerApi {
    @ApiOperation("上传文件")
    public UploadFileResult upload(MultipartFile multipartFile,
                                   String filetag,
                                   String businesskey,
                                   String metadata);
}
