package com.ming.seckill.rabbitMQ;

import com.ming.seckill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {
    private static final Logger logger = LoggerFactory.getLogger(MQSender.class);


    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendSeckillMessage(SeckillMessage message) {
        String msg = RedisService.beanToString(message);
        logger.info("send message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE,msg);
    }

//    public void send(Object message){
//        //把对象转为string
//        String msg = RedisService.beanToString(message);
//        logger.info("send message:"+msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
//    }
//
//    public void sendTopic(Object message){
//        //把对象转为string
//        String msg = RedisService.beanToString(message);
//        logger.info("send topic message:"+msg);
//        //need three args
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
//    }
//
//    public void sendFanout(Object message){
//        //把对象转为string
//        String msg = RedisService.beanToString(message);
//        logger.info("send fanout message:"+msg);
//        //need three args
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
//    }
//
//    public void sendHeaders(Object message){
//        //把对象转为string
//        String msg = RedisService.beanToString(message);
//        logger.info("send fanout message:"+msg);
//        MessageProperties properties = new MessageProperties();
//        //因为exchange 设置的是All，所以必须满足所有的header才可以放进去
//        properties.setHeader("header1","value1");
//        properties.setHeader("header2","value2");
//        Message object = new Message(msg.getBytes(),properties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",object);
//    }


}
