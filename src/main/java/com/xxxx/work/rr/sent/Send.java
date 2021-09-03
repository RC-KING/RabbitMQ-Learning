package com.xxxx.work.rr.sent;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * 工作队列-轮询模式-消息发送者
 */
public class Send {
    // 定义队列名称
    private final static String QUEUE_NAME = "work_rr";

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
            /*
             * 声明队列
             * 第一个参数queue：队列名称
             * 第二个参数durable：是否持久化
             * 第三个参数Exclusive：排他队列，如果一个队列被声明为排他队列，该队列仅对首次
             声明它的连接可见，并在连接断开时自动删除。
             *   这里需要注意三点：
             *     1. 排他队列是基于连接可见的，同一连接的不同通道是可以同时访问同一个
             连接创建的排他队列的。
             *     2. "首次"，如果一个连接已经声明了一个排他队列，其他连接是不允许建
             立同名的排他队列的，这个与普通队列不同。
             *     3. 即使该队列是持久化的，一旦连接关闭或者客户端退出，该排他队列都会
             被自动删除的。
             *     这种队列适用于只限于一个客户端发送读取消息的应用场景。
             * 第四个参数Auto-delete：自动删除，如果该队列没有任何订阅的消费者的话，该队列
             会被自动删除。
             *             这种队列适用于临时队列。
             */
            // 绑定队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 模拟多数据发送
            for (int i = 0; i < 16; i++) {
                // 创建消息
                String message = "Hello World!" + i;
                // 将产生的消息放入队列
                channel.basicPublish(""
                        , QUEUE_NAME,
                        null,
                        message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'"+i);
            }
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
