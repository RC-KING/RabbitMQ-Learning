package com.xxxx.exchanges.receive;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发布/订阅模式-广播模式-消息接收者1
 */
public class Recv01 {
    // 定义队列名称
    private final static String EXCHANGE_NAME = "exchange_fanout";

    public static void main(String[] args) {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.243.128");
        factory.setPort(5672);
        factory.setUsername("yeb");
        factory.setPassword("yeb");
        factory.setVirtualHost("/yeb");
        try {
            // 通过工厂创建连接
            Connection connection = factory.newConnection();
            // 获取通道
            Channel channel = connection.createChannel();
            // 绑定交换机
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
            // 获取队列名称
            String queueName = channel.queueDeclare().getQueue();
            // 将队列和交换机绑定
            channel.queueBind(queueName, EXCHANGE_NAME, "");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            // 获取消息
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };
            // 监听队列
            channel.basicConsume(queueName, true, deliverCallback, consumerTag
                    -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}