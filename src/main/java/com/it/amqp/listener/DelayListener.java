package com.it.amqp.listener;

import com.it.amqp.config.AmqpConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author: zhaowq
 * @since: 2018/7/5 16:44
 * @description: 延迟消息消费监听
 */

@Component
public class DelayListener implements ChannelAwareMessageListener {
    private final Logger logger = LoggerFactory.getLogger(DelayListener.class);
    public static CountDownLatch latch;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            String realMessage = new String(message.getBody());
            realMessage = String.valueOf(realMessage);
            logger.info("message:{}", realMessage);
            //模拟消息处理。如果当消息内容为 fail 的话，则需要抛出异常
        } catch (Exception e) {
            // 如果发生了异常，则将该消息重定向到缓冲队列，会在一定延迟之后自动重做
            channel.basicPublish(AmqpConfig.PER_QUEUE_TTL_EXCHANGE_NAME, AmqpConfig.DELAY_QUEUE_PER_QUEUE_TTL_NAME, null,
                    "The failed message will auto retry after a certain delay".getBytes());
        }

        if (latch != null) {
            latch.countDown();
        }
    }
}
