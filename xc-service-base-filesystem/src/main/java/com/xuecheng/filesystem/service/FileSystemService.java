package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.smartcardio.CommandAPDU;
import java.io.IOException;
import java.util.Map;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/20 9:41
 */
@Service
public class FileSystemService {
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;

    @Autowired
    FileSystemRepository fileSystemRepository;



        //上传文件

    public UploadFileResult upload(MultipartFile multipartFile,
                                   String filetag,
                                   String businesskey,
                                   String metadata){
        if(multipartFile == null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //1.将文件上传到fastdfs中，得到文件id
        String fileId = fdfs_upload(multipartFile);
        if(StringUtils.isEmpty(fileId)){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        //2.将文件id以及其他信息存储到mongodb中
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFiletag(filetag);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());
        if(StringUtils.isNotEmpty(metadata)){
            Map map = JSON.parseObject(metadata,Map.class);
            fileSystem.setMetadata(map);
        }
        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);

    }

    //上传文件到fastdfs

    /**
     *
     * @param multipartFile 文件
     * @return 文件id
     */
    private String  fdfs_upload(MultipartFile multipartFile){
        //初始化fastdfs环境
        initFastConfig();
        TrackerClient trackerClient = new TrackerClient();
        try {
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            byte[] bytes = multipartFile.getBytes();
            //得到文件原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            //得到扩展名
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_file1(bytes, ext, null);
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //初始化fastdfs环境
    private void initFastConfig(){
        //初始化tracker服务器
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            //抛异常
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
}
