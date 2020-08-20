package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 **/

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestFastDFS.class)
public class TestFastDFS {
    @Test
    public void testUpload() {
        try {
            //加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageclient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //上传文件
            String filePath = "d:/1590485465473.jpeg";
            String fileId = storageClient1.upload_file1(filePath, "jpeg", null);
            System.out.println(fileId);
            //group1/M00/00/00/wKhWgl8Sr26AP8VmAAkwHu_rOaA659.jpg
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //下载文件
    @Test
    public void download(){
        try {
            //加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageclient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //下载文件
            String fileId = "group1/M00/00/00/wKhWgl8S6PCAKyrNAANOM0jAG5E76.jpeg";
            byte[] bytes = storageClient1.download_file1(fileId);
            //使用输出流
            FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/logo.jpg"));
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
