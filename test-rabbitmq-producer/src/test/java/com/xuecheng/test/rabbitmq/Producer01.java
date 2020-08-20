package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq入门程序--生产者
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/13 17:17
 */
public class Producer01 {

    private static final String QUEUE = "helloworld";

    public static void main(String[] args) throws IOException, TimeoutException {
        //通过连接工厂创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机相当于一个独立的mq服务
        connectionFactory.setVirtualHost("/");
        //建立新连接
        Connection connection = null;
        Channel channel = null;
        try {
            //建立新连接
            connection = connectionFactory.newConnection();
            //创建会话通道
            channel = connection.createChannel();
            //声明队列
            channel.queueDeclare(QUEUE,true,false,false,null);
            //发送消息
            //消息内容
            String message = "hello worle 黑马程序员";
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println("send to rabbitmq"+message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            channel.close();
            connection.close();
        }

    }
}
