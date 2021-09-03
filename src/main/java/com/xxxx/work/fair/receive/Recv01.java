package com.xxxx.work.fair.receive;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 工作队列-公平模式-消息接收者1
 */
public class Recv01 {
    // 定义队列名称
    private final static String QUEUE_NAME = "work_fair";

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
            // 指定队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            // ---------------------之前旧版本的写法-------begin-----------
            // 匿名内部类
                    /*
                    // 获取消息
                    Consumer consumer = new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag,
                        Envelope envelope,
                        AMQP.BasicProperties properties,
                        byte[] body) throws IOException {
                            // 获取消息并在控制台打印
                            String message = new String(body, "utf-8");
                            System.out.println(" [x] Received '" + message + "'");
                        }
                    };
                    // 监听队列
                    channel.basicConsume(QUEUE_NAME, true, consumer);
                    */
            // ---------------------之前旧版本的写法--------end------------
            // 限制消费者每次只能取一条消息,处理完才能进行下一条的处理
            int perfetchCount = 1;
            channel.basicQos(perfetchCount);

            // 获取消息
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                // 模拟慢接收
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                // 手动确认,一条一条的确认
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            // 监听队列
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag
                    -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}