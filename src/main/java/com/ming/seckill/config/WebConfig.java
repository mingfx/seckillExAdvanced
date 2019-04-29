package com.ming.seckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Override
    //controller方法会回调这个方法，把需要的参数进行设置...感觉不如用拦截器，通过threadlocal对象去拿，这样每次都要去查redis
    //TODO 可以利用拦截器去做
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //把自定义的参数解析resolver注册进来
        argumentResolvers.add(userArgumentResolver);
    }
}
