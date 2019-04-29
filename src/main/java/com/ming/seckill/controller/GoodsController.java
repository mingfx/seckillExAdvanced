package com.ming.seckill.controller;

import com.ming.seckill.domain.SeckillUser;
import com.ming.seckill.redis.GoodsKey;
import com.ming.seckill.redis.RedisService;
import com.ming.seckill.redis.SeckillUserKey;
import com.ming.seckill.result.Result;
import com.ming.seckill.service.SeckillGoodsService;
import com.ming.seckill.service.SeckillUserService;
import com.ming.seckill.vo.GoodsDetailVo;
import com.ming.seckill.vo.LoginVo;
import com.ming.seckill.vo.SeckillGoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

//    @Autowired
//    ApplicationContext applicationContext;

    //优化：直接返回页面源代码，利用页面缓存
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toList(Model model,HttpServletResponse response,
                         HttpServletRequest request,
                         SeckillUser seckillUser){
//        if (StringUtils.isEmpty(cookieToken)&& StringUtils.isEmpty(paramToken)){
//            return "login";
//        }
//        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
//        SeckillUser seckillUser = seckillUserService.getByToken(response,token);

        //return "goods_list";
        //1.从缓存中取
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        //没有缓存，准备数据
        model.addAttribute("user",seckillUser);
        //查询商品列表
        List<SeckillGoodsVo> list = seckillGoodsService.getListSeckillGoodsVo();
        model.addAttribute("goodsList",list);
        //手动渲染,利用thymeleafViewResolver(之前是springboot来渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",webContext);
        if (!StringUtils.isEmpty(html)){
            //缓存到redis中，有效期咋GoodsKey中设置的一分钟
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }

    //优化：url缓存，针对不同的goodsId有不同的缓存，所以叫url级
    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String toDetail(Model model, HttpServletResponse response,
                           HttpServletRequest request,
                           @PathVariable("goodsId") long goodsId,
                           SeckillUser seckillUser){
        //1.从缓存中取
        String html = redisService.get(GoodsKey.getGoodsDetail,""+ goodsId,String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        //数据准备
        SeckillGoodsVo seckillGoodsVo = seckillGoodsService.getSeckillGoodsVoById(goodsId);
        long startTime = seckillGoodsVo.getStartDate().getTime();
        long endTime = seckillGoodsVo.getEndDate().getTime();
        long now = new Date().getTime();
        int remainSeconds = 0;
        int seckillStatus = 0;//0未开始，1正在进行，2已结束
        if (now<startTime){
            //秒杀未开始
            seckillStatus = 0;
            remainSeconds = (int) ((startTime - now)/1000);
        }else if (now > endTime){
            //秒杀已结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀正在进行
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("goods",seckillGoodsVo);
        model.addAttribute("user",seckillUser);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("seckillStatus", seckillStatus);
        //return "goods_detail";
        //手动渲染,利用thymeleafViewResolver(之前是springboot来渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",webContext);
        if (!StringUtils.isEmpty(html)){
            //缓存到redis中，有效期咋GoodsKey中设置的一分钟
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;
    }

    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                        Model model, SeckillUser user,
                                        @PathVariable("goodsId")long goodsId) {
        SeckillGoodsVo goods = seckillGoodsService.getSeckillGoodsVoById(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int seckillStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setSeckillStatus(seckillStatus);
        return Result.success(vo);
    }
}
