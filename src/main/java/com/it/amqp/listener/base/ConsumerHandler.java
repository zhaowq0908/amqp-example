package com.it.amqp.listener.base;

/**
 * @author: zhaowq
 * @since: 2018/6/26 9:50
 * @description: 消息处理扩展接口
 */
public interface ConsumerHandler<T> {

    /**
     * 消费前校验消息的正确性，由具体消费者定义规则
     * @param message
     * @return
     */
    boolean checkMessage(T message);

    /**
     * 处理消息
     * @param message
     * @throws Exception
     */
    void onMessage(T message) throws Exception;
}
