package com.ming.seckill.controller;

import com.ming.seckill.domain.OrderInfo;
import com.ming.seckill.domain.SeckillUser;
import com.ming.seckill.exception.GlobalException;
import com.ming.seckill.redis.GoodsKey;
import com.ming.seckill.redis.OrderKey;
import com.ming.seckill.redis.RedisService;
import com.ming.seckill.result.CodeMsg;
import com.ming.seckill.result.Result;
import com.ming.seckill.service.OrderService;
import com.ming.seckill.service.SeckillGoodsService;
import com.ming.seckill.service.SeckillUserService;
import com.ming.seckill.vo.GoodsDetailVo;
import com.ming.seckill.vo.OrderDetailVo;
import com.ming.seckill.vo.SeckillGoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    //优化：直接返回页面源代码，利用页面缓存
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toList(Model model,HttpServletResponse response,
                         HttpServletRequest request,
                         SeckillUser seckillUser){
//        //1.从缓存中取
//        String html = redisService.get(OrderKey.getOrderList,""+seckillUser.getId(),String.class);
//        if (!StringUtils.isEmpty(html)){
//            return html;
//        }
//        //没有缓存，准备数据
//        model.addAttribute("user",seckillUser);
//        //查询商品列表
//
//        //手动渲染,利用thymeleafViewResolver(之前是springboot来渲染
//        WebContext webContext = new WebContext(request,response,request.getServletContext(),
//                request.getLocale(),model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",webContext);
//        if (!StringUtils.isEmpty(html)){
//            //缓存到redis中，有效期咋GoodsKey中设置的一分钟
//            redisService.set(OrderKey.getOrderList,""+seckillUser.getId(),html);
//        }
//        return html;
        //TODO
        return null;
    }

    //优化：url缓存，针对不同的goodsId有不同的缓存，所以叫url级
    @RequestMapping(value = "/to_detail/{orderId}",produces = "text/html")
    @ResponseBody
    public String toDetail(Model model, HttpServletResponse response,
                           HttpServletRequest request,
                           @PathVariable("orderId") long orderId,
                           SeckillUser seckillUser){
        if (seckillUser==null){
            //TODO 应该让他去登陆，还应该判断一下是不是他的订单，如果不是不能看
            throw new GlobalException(CodeMsg.SESSION_ERROR);
        }
        //1.从缓存中取
        String html = redisService.get(OrderKey.getOrderDetailHtml,""+ orderId,String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        //数据准备
        OrderInfo orderInfo = orderService.getOrderInfoByOrderId(orderId);
        if (orderInfo==null){
            throw new GlobalException(CodeMsg.ORDER_NOT_EXIST);
        }
        //为了显示商品图片等信息，传一个goods进去
        SeckillGoodsVo goodsVo = seckillGoodsService.getSeckillGoodsVoById(orderInfo.getGoodsId());
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        //手动渲染,利用thymeleafViewResolver(之前是springboot来渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("order_detail",webContext);
        if (!StringUtils.isEmpty(html)){
            //缓存到redis中，有效期咋GoodsKey中设置的一分钟
            redisService.set(OrderKey.getOrderDetailHtml,""+ orderId,html);
        }
        return html;
    }

    @RequestMapping(value="/detail/{orderId}")
    @ResponseBody
    public Result<OrderDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                        Model model, SeckillUser user,
                                        @PathVariable("orderId")long orderId) {
        //TODO 用拦截器判断
        if (user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderInfoByOrderId(orderId);
        if (orderInfo==null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        SeckillGoodsVo goodsVo = seckillGoodsService.getSeckillGoodsVoById(orderInfo.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goodsVo);
        orderDetailVo.setOrder(orderInfo);
        return Result.success(orderDetailVo);
    }
}
