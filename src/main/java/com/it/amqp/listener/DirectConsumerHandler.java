package com.it.amqp.listener;

import com.it.amqp.listener.base.AbstractConsumerHandler;
import com.it.amqp.message.ExampleMessage;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author: zhaowq
 * @since: 2018/6/25 17:18
 * @description: 点对点模式
 */
@Component
@RabbitListener(queues = "direct_queue")
public class DirectConsumerHandler extends AbstractConsumerHandler<ExampleMessage> {

    @RabbitHandler
    @Override
    public void onMessage(ExampleMessage message) {
        if (!checkMessage(message))
            return;
        logger.info("direct message:{}", message.toString());
        //throw new MqException("玩完了");
    }
}
