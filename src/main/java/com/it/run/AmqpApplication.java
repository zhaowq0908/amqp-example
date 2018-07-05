package com.it.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: zhaowq
 * @since: 2018/7/5 16:34
 * @description: 应用启动入口
 */
@SpringBootApplication
@ComponentScan("com.it.amqp")
public class AmqpApplication {
    public static void main(String[] args) {
        SpringApplication.run(AmqpApplication.class, args);
    }
}
