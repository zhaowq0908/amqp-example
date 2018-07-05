package com.it.amqp.listener.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: zhaowq
 * @since: 2018/6/26 9:38
 * @description: 消息扩展基类
 */
public abstract class AbstractConsumerHandler<T> implements ConsumerHandler<T> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean checkMessage(T message) {
        return true;
    }
}
