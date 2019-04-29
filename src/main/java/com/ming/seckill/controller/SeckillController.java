package com.ming.seckill.controller;

import com.ming.seckill.domain.OrderInfo;
import com.ming.seckill.domain.SeckillGoods;
import com.ming.seckill.domain.SeckillOrder;
import com.ming.seckill.domain.SeckillUser;
import com.ming.seckill.rabbitMQ.MQSender;
import com.ming.seckill.rabbitMQ.SeckillMessage;
import com.ming.seckill.redis.GoodsKey;
import com.ming.seckill.redis.OrderKey;
import com.ming.seckill.redis.RedisService;
import com.ming.seckill.redis.SeckillKey;
import com.ming.seckill.result.CodeMsg;
import com.ming.seckill.result.Result;
import com.ming.seckill.service.OrderService;
import com.ming.seckill.service.SeckillGoodsService;
import com.ming.seckill.service.SeckillService;
import com.ming.seckill.vo.SeckillGoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    //实现initializingBean从而在初始化时将库存加载
    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    private Map<Long,Boolean> lcoalOverMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<SeckillGoodsVo> goodsList = seckillGoodsService.getListSeckillGoodsVo();
        if (goodsList==null){
            return;
        }
        for (SeckillGoodsVo good :
                goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock,""+good.getId(),good.getStockCount());
            lcoalOverMap.put(good.getId(),false);
        }

    }
    //get post有什么区别：真正区别：get幂等（获取数据，调用多少次都是一样的）post（非幂等，提交数据
    //优化：直接返回订单。为啥要这么做呢？不是应该避免返回reslut这些信息吗
    @PostMapping("/do_seckill")
    @ResponseBody
    public Result<Integer> doSeckill(Model model,
                            SeckillUser seckillUser,
                            @RequestParam("goodsId") long goodsId){
        if (seckillUser==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        model.addAttribute("user",seckillUser);
        //内存标记，为了进一步减少网络开销，将库存不够的直接存在内存中，redis也不要访问
        boolean over = lcoalOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.SECKILL_RUNOUT);
        }
        //预减库存，减一，返回结果值
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock,""+goodsId);
        if (stock < 0){
            lcoalOverMap.put(goodsId,true);
            return Result.error(CodeMsg.SECKILL_RUNOUT);
        }
        //判断是否已经秒杀过了(做了优化，查缓存里的订单）
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(),goodsId);
        if (seckillOrder!=null){
            //重复秒杀，返回失败信息
            return Result.error(CodeMsg.SECKILL_REPEAT);
        }
        //入队
        redisService.set(SeckillKey.seckillResult,seckillUser.getId()+"_"+goodsId,Result.seckillWait());
        SeckillMessage message = new SeckillMessage();
        message.setSeckillUser(seckillUser);
        message.setGoodsId(goodsId);
        sender.sendSeckillMessage(message);
        return Result.seckillWait();//排队中
        /*
         * 优化之前的
        //判断库存（还没优化？
        SeckillGoodsVo goodsVo = seckillGoodsService.getSeckillGoodsVoById(goodsId);
        int stock = goodsVo.getStockCount();
        if (stock<0){
            //库存不足，返回失败信息
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否已经秒杀过了(做了优化，查缓存里的订单）
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(),goodsId);
        if (seckillOrder!=null){
            //重复秒杀，返回失败信息
            return Result.error(CodeMsg.SECKILL_REPEAT);
        }
        //秒杀,成功返回订单详情
        OrderInfo orderInfo = seckillService.seckill(seckillUser,goodsVo);
        return Result.success(orderInfo);
         */
    }

    /**
     *orderId：成功
     * 500555：排队中
     */
    @GetMapping("/result")
    @ResponseBody
    public Result<Long> getSeckillResult(Model model,
                                     SeckillUser seckillUser,
                                     @RequestParam("goodsId") long goodsId) {
        if (seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //long result = seckillService.getSeckillResult(seckillUser.getId(),goodsId);
        Result<Long> result = seckillService.getSeckillResult(seckillUser.getId(),goodsId);
        return result;
    }

    //reset 正常应该是通过管理后台来设置
    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<SeckillGoodsVo> goodsList = seckillGoodsService.getListSeckillGoodsVo();
        for(SeckillGoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getSeckillGoodsStock, ""+goods.getId(), 10);
            lcoalOverMap.put(goods.getId(), false);
        }
//        redisService.delete(OrderKey.getSeckillOrderByUidGid,);
//        redisService.delete(SeckillKey.seckillResult,);
        seckillService.reset(goodsList);
        return Result.success(true);
    }
}
