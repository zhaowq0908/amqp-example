package com.it.amqp.config;

import com.it.amqp.listener.DelayListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: zhaowq
 * @since: 2018/7/5 16:40
 * @description: amqp配置
 */
@Configuration
public class AmqpConfig {
    /**
     * 指定序列化
     *
     * @return
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setCreateMessageIds(true);
        return jackson2JsonMessageConverter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(MessageConverter jsonMessageConverter, ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        rabbitTemplate.setConnectionFactory(connectionFactory);
        return rabbitTemplate;
    }

    /********************点对点模式***********************/
    @Bean
    public Queue directQueue() {
        return new Queue("direct_queue");
    }

    /********************广播模式***********************/
    @Bean
    public Queue messageA() {
        return new Queue("fanout_A");
    }

    @Bean
    public Queue messageB() {
        return new Queue("fanout_B");
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("example_fanout_exchange");
    }

    @Bean
    public Binding bindingExchangeA(Queue messageA, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(messageA).to(fanoutExchange);
    }

    @Bean
    public Binding bindingExchangeB(Queue messageB, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(messageB).to(fanoutExchange);
    }

    /********************主题模式***********************/
    @Bean
    public Queue queueMessageA() {
        return new Queue("topic.messageA");
    }

    @Bean
    public Queue queueMessageB() {
        return new Queue("topic.messageB");
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("topic_exchange");
    }

    @Bean
    public Binding bindingExchangeMessageA(Queue queueMessageA, TopicExchange exchange) {
        return BindingBuilder.bind(queueMessageA).to(exchange).with("topic.messageA");
    }

    @Bean
    public Binding bindingExchangeMessageB(Queue queueMessageB, TopicExchange exchange) {
        return BindingBuilder.bind(queueMessageB).to(exchange).with("topic.#");
    }
    /********************example end ***********************/


    /**
     * 方式一：延迟消息缓存队列
     * 发送到该队列的message会在一段时间后过期进入到delay_process_queue
     * 每个message可以控制自己的失效时间
     */
    public final static String DELAY_QUEUE_PER_MESSAGE_TTL_NAME = "delay_queue_per_message_ttl";
    /**
     * 方式二：延迟消息（该队列所有消息）缓存队列
     * 发送到该队列的消息会在一段时间后过期进入到 delay_process_queue
     * 队列里所有的message都有统一的失效时间
     */
    public final static String DELAY_QUEUE_PER_QUEUE_TTL_NAME = "delay_queue_per_queue_ttl";
    public final static int QUEUE_EXPIRATION = 4000;

    /**
     * 延迟消息处理队列
     * 缓存消息失效后进入的队列，即实际的消费队列
     */
    public final static String DELAY_PROCESS_QUEUE_NAME = "delay_process_queue";

    /**
     * 延迟消息处理交换机 DLX dead letter exchange
     */
    public final static String DELAY_EXCHANGE_NAME = "delay_exchange";

    /**
     * 延迟消息处理队列失败，路由到的新的交换机 delay_queue_per_queue_ttl
     */
    public final static String PER_QUEUE_TTL_EXCHANGE_NAME = "per_queue_ttl_exchange";

    /**
     * 创建延迟消息处理队列，也就是实际消费队列 delay_process_queue
     *
     * @return
     */
    @Bean
    public Queue delayProcessQueue() {
        return QueueBuilder.durable(DELAY_PROCESS_QUEUE_NAME).build();
    }

    /**
     * 创建延迟消息处理交换机 DLX dead letter exchange
     *
     * @return
     */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    /**
     * 将DLX绑定到实际消费队列
     *
     * @param delayProcessQueue
     * @param delayExchange
     * @return
     */
    @Bean
    public Binding dlxBinding(Queue delayProcessQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayProcessQueue)
                .to(delayExchange)
                .with(DELAY_PROCESS_QUEUE_NAME);
    }

    /**
     * 创建延迟消息缓存队列，按每个消息进行延迟，delay_queue_per_message_ttl
     *
     * @return
     */
    @Bean
    public Queue delayQueuePerMessageTTL() {
        return QueueBuilder.durable(DELAY_QUEUE_PER_MESSAGE_TTL_NAME)
                .withArgument("x-dead-letter-exchange", DELAY_EXCHANGE_NAME) // DLX，dead letter发送到的exchange
                .withArgument("x-dead-letter-routing-key", DELAY_PROCESS_QUEUE_NAME) // dead letter携带的routing key
                .build();
    }

    /**
     * 创建延迟消息交换机 per_queue_ttl_exchange
     *
     * @return
     */
    @Bean
    public DirectExchange perQueueTTLExchange() {
        return new DirectExchange(PER_QUEUE_TTL_EXCHANGE_NAME);
    }

    /**
     * 创建延迟消息缓存队列，整个队列所有消息都按此时间延迟 delay_queue_per_queue_ttl
     *
     * @return
     */
    @Bean
    public Queue delayQueuePerQueueTTL() {
        return QueueBuilder.durable(DELAY_QUEUE_PER_QUEUE_TTL_NAME)
                .withArgument("x-dead-letter-exchange", DELAY_EXCHANGE_NAME) // DLX
                .withArgument("x-dead-letter-routing-key", DELAY_PROCESS_QUEUE_NAME) // dead letter携带的routing key
                .withArgument("x-message-ttl", QUEUE_EXPIRATION) // 设置队列的过期时间
                .build();
    }

    /**
     * 将 per_queue_ttl_exchange 绑定到 delay_queue_per_queue_ttl 队列
     *
     * @param delayQueuePerQueueTTL
     * @param perQueueTTLExchange
     * @return
     */
    @Bean
    public Binding queueTTLBinding(Queue delayQueuePerQueueTTL, DirectExchange perQueueTTLExchange) {
        return BindingBuilder.bind(delayQueuePerQueueTTL)
                .to(perQueueTTLExchange)
                .with(DELAY_QUEUE_PER_QUEUE_TTL_NAME);
    }

    /**
     * 定义消费者监听，并声明监听队列 即延迟消息处理队列 delay_process_queue
     *
     * @param connectionFactory
     * @param delayListener
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer processContainer(ConnectionFactory connectionFactory, DelayListener delayListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(DELAY_PROCESS_QUEUE_NAME); // 监听delay_process_queue
        container.setMessageListener(new MessageListenerAdapter(delayListener));
        return container;
    }

}
