package com.it.amqp.listener;

import com.it.amqp.listener.base.AbstractConsumerHandler;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author: zhaowq
 * @since: 2018/6/26 10:39
 * @description: 测试广播模式
 */
@Component
@RabbitListener(queues = {"fanout_A", "fanout_B"})//这里多队列，是为了方便测试，实际开发中不建议这么定义
public class FanoutConsumerHandler extends AbstractConsumerHandler<String> {

    @RabbitHandler
    @Override
    public void onMessage(String message) throws Exception {
        if (!checkMessage(message))
            return;
        logger.info("fanout message :{}", message);
    }
}
