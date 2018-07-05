package com.it.amqp.message;

import java.io.Serializable;

/**
 * @author: zhaowq
 * @since: 2018/6/26 10:32
 * @description: 消息实体例子
 */
public class ExampleMessage<T> implements Serializable{
    private static final long serialVersionUID = 1L;

    private T message;

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ExampleMessage{" +
                "message=" + message +
                '}';
    }
}
