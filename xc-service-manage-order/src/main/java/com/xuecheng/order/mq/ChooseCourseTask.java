package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/20 8:18
 */
@Component
public class ChooseCourseTask {
    @Autowired
    TaskService taskService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);
    //定时发送加选课任务
    @Scheduled(cron = "0/3 * * * * *")
    public void sendChooseCourseTask(){
        //得到1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findXcTaskList(time, 100);
        System.out.println(xcTaskList);
        for(XcTask xcTask:xcTaskList){
            if(taskService.getTask(xcTask.getId(),xcTask.getVersion())>0){
                String ex = xcTask.getMqExchange();
                String routingkey = xcTask.getMqRoutingkey();
                taskService.publish(xcTask,ex,routingkey);
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChooseCourseTask(XcTask xcTask){
        if(xcTask != null && StringUtils.isNotEmpty(xcTask.getId())){
            taskService.finishTask(xcTask.getId());
        }
    }


    //定义任务调试策略
    //@Scheduled(cron = "0/3 * * * * *")//每隔3秒执行
    public void task1(){
        LOGGER.info("==============测试任务1开始=================");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("==============测试任务1结束=================");
    }
}
