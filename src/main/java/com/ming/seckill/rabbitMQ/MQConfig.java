package com.ming.seckill.rabbitMQ;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MQConfig {
    public static final String SECKILL_QUEUE = "seckill.queue";
    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String TOPIC_EXCHANGE= "topicExchange";
    public static final String FANOUT_EXCHANGE= "fanoutExchange";
    public static final String HEADERS_EXCHANGE= "headersExchange";
    public static final String HEADERS_QUEUE = "headers.queue1";


//    public static final String ROUTING_KEY1= "topic.key1";
//    public static final String ROUTING_KEY2= "topic.#";//#是通配符，代表0个或多个单词

//    @Bean
//    public Queue seckillQueue(){
//        return new Queue(SECKILL_QUEUE,true);
//    }

    /**
     * 有四种交换机Exchange模式
     * 交换机：发送的时候并不直接发送到队列上，先发送到交换机再发送到队列，做一次路由
     * direct模式，最简单的，指定队列名
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }

    /**
     * Topic模式
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1,true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2,true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    //根据不同的key，经过exchange，绑定到不同的queue上
    @Bean
    public Binding topicBinding1(){
        //绑定
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
    }

    /**
     * Fanout模式,,也就是广播模式，可以发给多个queue
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding fanoutBinding1(){
        //绑定
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2(){
        //绑定
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    /**
     * Header模式,可以设置需要满足不同的kv条件，才放消息
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headersQueue1(){
        return new Queue(HEADERS_QUEUE,true);
    }
    @Bean
    public Binding headersBinding2(){
        //绑定
        Map<String,Object> map = new HashMap<>();
        map.put("header1","value1");
        map.put("header2","value2");

        return BindingBuilder.bind(headersQueue1()).to(headersExchange()).whereAll(map).match();
    }
}
