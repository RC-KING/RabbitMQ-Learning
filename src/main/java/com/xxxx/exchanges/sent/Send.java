package com.xxxx.exchanges.sent;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * 发布/订阅模式-广播模式-消息发送者
 */
public class Send {
    // 定义交换机名称
    private final static String EXCHANGE_NAME = "exchange_fanout";

    public static void main(String[] args) {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置IP
        factory.setHost("192.168.243.128");
        factory.setUsername("yeb");
        factory.setVirtualHost("/yeb");
        factory.setPassword("yeb");
        // 端口是默认的
        factory.setPort(5672);
        Connection connection = null;
        Channel channel = null;
        try {
            // 通过工厂创建连接
            connection = factory.newConnection();
            // 获取信道
            channel = connection.createChannel();
            // 绑定交换机
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);




                // 创建消息
                String message = "Hello World!" ;
                // 将产生的消息放入队列
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭通道
                if (null != channel && channel.isOpen())
                    channel.close();
                // 关闭连接
                if (null != connection && connection.isOpen())
                    connection.close();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
