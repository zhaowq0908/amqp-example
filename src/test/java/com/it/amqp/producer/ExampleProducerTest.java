package com.it.amqp.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it.amqp.config.AmqpConfig;
import com.it.amqp.listener.DelayListener;
import com.it.amqp.message.ExampleMessage;
import org.junit.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author: zhaowq
 * @since: 2018/6/25 17:25
 * @description: 测试发送消息
 */
public class ExampleProducerTest extends BaseTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 发送到延迟消息缓存队列
     * @throws InterruptedException
     */
    @Test
    public void testDelayQueuePerMessageTTL() throws InterruptedException {
        DelayListener.latch = new CountDownLatch(3);
        for (int i = 1; i <= 3; i++) {
            String expiration = String.valueOf(i * 1000);
            //消息延迟
            rabbitTemplate.convertAndSend(AmqpConfig.DELAY_QUEUE_PER_MESSAGE_TTL_NAME,
                    (Object) ("Message From delay_queue_per_message_ttl with expiration " + expiration), new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            //设置per-message的失效时间
                            message.getMessageProperties().setExpiration(expiration);
                            return message;
                        }
                    });
        }
        DelayListener.latch.await();
    }

    @Test
    public void testDelayQueuePerQueueTTL() throws InterruptedException {
        DelayListener.latch = new CountDownLatch(3);
        for (int i = 1; i <= 3; i++) {
            //队列延迟
            rabbitTemplate.convertAndSend(AmqpConfig.DELAY_QUEUE_PER_QUEUE_TTL_NAME,
                    "Message From delay_queue_per_queue_ttl with expiration " + AmqpConfig.QUEUE_EXPIRATION);
        }
        DelayListener.latch.await();
    }

    @Test
    public void testFailMessage() throws InterruptedException {
        DelayListener.latch = new CountDownLatch(6);
        for (int i = 1; i <= 3; i++) {
            rabbitTemplate.convertAndSend(AmqpConfig.DELAY_PROCESS_QUEUE_NAME, "message");
        }
        DelayListener.latch.await();
    }

    /**
     * 点对点模式(实体类型)
     */
    @Test
    public void sendDirectMessage() {
        for (int i = 1000; i < 1010; i++) {
            Map<String, Object> message = new HashMap<>(2);
            message.put("messageId", i);
            message.put("messageBody", "点对点模式：测试消息发送");
            message.put("date", new Date());
            ExampleMessage<Map<String, Object>> exampleMessage = new ExampleMessage<>();
            exampleMessage.setMessage(message);
            rabbitTemplate.convertAndSend("direct_queue", exampleMessage);
        }
        logger.info("点对点模式：消息发送完成。。。");
    }

    /**
     * 广播模式（jsonString类型）
     */
    @Test
    public void sendFanoutMessage() throws Exception {
        for (int i = 1000; i < 1010; i++) {
            Map<String, Object> message = new HashMap<>(2);
            message.put("messageId", i);
            message.put("messageBody", "广播模式：测试消息发送");
            ObjectMapper mapper = new ObjectMapper();
            rabbitTemplate.convertAndSend("example_fanout_exchange", "", mapper.writeValueAsString(message));
        }
        logger.info("广播模式：消息发送完成。。。");
    }

    /**
     * 主题模式(集合类型)
     */
    @Test
    public void sendTopicMessage() {
        for (int i = 1000; i < 1010; i++) {
            Map<String, Object> message = new HashMap<>(2);
            message.put("messageId", i);
            message.put("messageBody", "主题模式：测试消息发送");
            rabbitTemplate.convertAndSend("topic_exchange", "topic.messageB.messageB", message);
        }
        logger.info("主题模式：消息发送完成。。。");
    }
}
