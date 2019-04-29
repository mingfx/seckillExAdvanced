package com.ming.seckill.controller;

import com.ming.seckill.domain.User;
import com.ming.seckill.rabbitMQ.MQReceiver;
import com.ming.seckill.rabbitMQ.MQSender;
import com.ming.seckill.redis.RedisService;
import com.ming.seckill.redis.UserKey;
import com.ming.seckill.result.CodeMsg;
import com.ming.seckill.result.Result;
import com.ming.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class Demo {
    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @Autowired
    MQReceiver receiver;

    @RequestMapping("/")
    @ResponseBody
    String home(){
        return "hello world!!";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello(){
        return Result.success("hello,imooc");
    }

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq(){
//        sender.send("hello,ming");
//        return Result.success("hello,imooc");
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> topic(){
//        sender.sendTopic("hello,ming");
//        return Result.success("hello,imooc");
//    }
//    //可以用swagger
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> fanout(){
//        sender.sendFanout("hello,ming");
//        return Result.success("hello,imooc");
//    }
//
//    //可以用swagger
//    @RequestMapping("/mq/headers")
//    @ResponseBody
//    public Result<String> headers(){
//        sender.sendHeaders("hello,ming");
//        return Result.success("hello,imooc");
//    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","ming");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx(){
        return Result.success(userService.tx());
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User(1,"1111");
        Boolean ret = redisService.set(UserKey.getById,""+1,user);
        return Result.success(true);
    }
}
