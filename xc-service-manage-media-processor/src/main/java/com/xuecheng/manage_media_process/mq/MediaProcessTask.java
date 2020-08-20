package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.omg.CORBA.StringHolder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/14 10:19
 */
@Component
public class MediaProcessTask {
    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;
    @Value("${xc-service-manage-media.video-location}")
    String serverPath;

    @Autowired
    MediaFileRepository mediaFileRepository;

    //接收视频消息并处理
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg){
        //1.解析消息内容，拿到mediaId
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");
        //2.拿Id查数据库文件信息
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if(!optional.isPresent()){
            return;
        }
        MediaFile mediaFile = optional.get();
        String fileType = mediaFile.getFileType();
        if(!fileType.equals("avi")){
            mediaFile.setProcessStatus("303004");//无需处理
            mediaFileRepository.save(mediaFile);
            return;
        }else {
            mediaFile.setProcessStatus("303001");//处理中
            mediaFileRepository.save(mediaFile);
        }
        //3.使用工具类将avi生成mp4
        String video_path = serverPath + mediaFile.getFilePath() + mediaFile.getFileName();
        String mp4_name = mediaFile.getFileId() + ".mp4";
        String mp4floder_path = serverPath + mediaFile.getFilePath();
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4floder_path);
        String result = mp4VideoUtil.generateMp4();
        if(result == null || !result.equals("success")){
            //失败
            mediaFile.setProcessStatus("303003");
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }

        //4.将mp4转成m3u8和ts文件


        String mp4_video_path = serverPath + mediaFile.getFilePath() + mp4_name;
        //m3u8文件名
        String m3u8_name = mediaFile.getFileId() + ".m3u8";
        //m3u8所在目录
        String m3u8folder_path = serverPath + mediaFile.getFilePath() + "/hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path,mp4_video_path,m3u8_name,m3u8folder_path);
        String tsResult = hlsVideoUtil.generateM3u8();
        if(tsResult == null || !tsResult.equals("success")){
            //失败
            mediaFile.setProcessStatus("303003");
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //成功
        //获取ts文件列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        mediaFile.setProcessStatus("303002");

        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);

        //保存fileUrl
        String fileUrl = mediaFile.getFilePath() + "hls/" + m3u8_name;
        mediaFile.setFileUrl(fileUrl);

        mediaFileRepository.save(mediaFile);
    }
}
