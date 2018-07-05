package com.it.amqp.producer;

import com.it.run.AmqpApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: zhaowq
 * @since: 2018/6/25 17:26
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AmqpApplication.class)
public class BaseTest {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void before() {
        logger.info("测试开始>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @After
    public void after() {
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<测试完成");
    }
}
