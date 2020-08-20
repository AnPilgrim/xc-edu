package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq入门程序--消费者
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/7/13 17:49
 */
public class Consumer01 {

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
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE,true,false,false,null);
        //消费方法
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            //当接收消息，此方法被调用
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String exchange = envelope.getExchange();
                long deliveryTag = envelope.getDeliveryTag();
                String message = new String(body,"utf-8");
                System.out.println("receive message:"+message);
            }
        };
        //监听队列
        channel.basicConsume(QUEUE,true,defaultConsumer);
    }
}
