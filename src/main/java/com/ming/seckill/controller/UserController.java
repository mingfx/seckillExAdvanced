package com.ming.seckill.controller;

import com.ming.seckill.domain.SeckillUser;
import com.ming.seckill.redis.RedisService;
import com.ming.seckill.result.Result;
import com.ming.seckill.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<SeckillUser> info(Model model, SeckillUser user) {
        //测试单纯获取user的qps
        return Result.success(user);
    }

}