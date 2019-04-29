package com.ming.seckill.service;

import com.ming.seckill.domain.OrderInfo;
import com.ming.seckill.domain.SeckillOrder;
import com.ming.seckill.domain.SeckillUser;
import com.ming.seckill.exception.GlobalException;
import com.ming.seckill.redis.OrderKey;
import com.ming.seckill.redis.RedisService;
import com.ming.seckill.redis.SeckillKey;
import com.ming.seckill.result.Result;
import com.ming.seckill.vo.SeckillGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeckillService {

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo seckill(SeckillUser seckillUser, SeckillGoodsVo goodsVo)
            throws GlobalException{
        try {
            //减库存，原子操作
            boolean success = seckillGoodsService.reduceStock(goodsVo);
            if (success){
                //写订单(两个表）
                return orderService.createOrder(seckillUser,goodsVo);
            }else {
                setGoodsOver(goodsVo.getId());
                return null;
            }

        } catch (GlobalException e) {
            throw e;
        }
    }


    public Result<Long> getSeckillResult(Long userId, long goodsId){
//        SeckillOrder order = redisService.get(OrderKey.getSeckillOrderByUidGid,
//                userId+"_"+goodsId, SeckillOrder.class);
//        if (order!=null){
//            //success
//            return order.getId();
//        }else {
//            boolean isOver = getGoodsOver(goodsId);
//            if (isOver){
//                return -1;
//            }else {
//                return 0;
//            }
//        }

        //直接从redis中查result
        Result result = redisService.get(SeckillKey.seckillResult,userId+"_"+goodsId,Result.class);
        return result;
    }

    private boolean getGoodsOver(long goodsId) {
        //有这个key说明卖完了
        return redisService.exists(SeckillKey.isGoodsOver,""+goodsId);
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver,""+goodsId,true);
    }

    public void reset(List<SeckillGoodsVo> goodsList) {
//        seckillGoodsService.resetStock(goodsList);
////        orderService.deleteOrders();
    }
}
