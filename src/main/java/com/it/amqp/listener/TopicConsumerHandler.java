package com.it.amqp.listener;

import com.it.amqp.listener.base.AbstractConsumerHandler;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: zhaowq
 * @since: 2018/6/26 11:18
 * @description: 主题模式
 */
@Component
@RabbitListener(queues = {"topic.messageA", "topic.messageB"})//这里多队列，是为了方便测试，实际开发中不建议这么定义
public class TopicConsumerHandler extends AbstractConsumerHandler<Map<String, Object>> {

    @RabbitHandler
    @Override
    public void onMessage(Map<String, Object> message) throws Exception {
        if (!checkMessage(message))
            return;
        logger.info("topic message :{}", message);
    }
}
