package com.ming.seckill.service;

import com.ming.seckill.dao.OrderDAO;
import com.ming.seckill.domain.OrderInfo;
import com.ming.seckill.domain.SeckillOrder;
import com.ming.seckill.domain.SeckillUser;
import com.ming.seckill.exception.GlobalException;
import com.ming.seckill.redis.OrderKey;
import com.ming.seckill.redis.RedisService;
import com.ming.seckill.result.CodeMsg;
import com.ming.seckill.vo.SeckillGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderDAO orderDAO;

    @Autowired
    RedisService redisService;

    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId,long goodsId){
        return redisService.get(OrderKey.getSeckillOrderByUidGid,userId+"_"+goodsId,SeckillOrder.class);
       // return orderDAO.getSeckillOrderByUserIdGoodsId(userId,goodsId);
    }

    public OrderInfo getOrderInfoByOrderId(long orderId){
        return orderDAO.getOrderInfoByOrderId(orderId);
    }

    public List<OrderInfo> getOrderInfoListByUserId(long userId){
        return orderDAO.getOrderInfoListByUserId(userId);
    }

    @Transactional
    public OrderInfo createOrder(SeckillUser seckillUser, SeckillGoodsVo goodsVo) throws GlobalException{
        //orderInfo
        OrderInfo newOrder = new OrderInfo();
        newOrder.setCreateDate(new Date());
        newOrder.setGoodsCount(1);
        newOrder.setGoodsName(goodsVo.getGoodsName());
        newOrder.setGoodsId(goodsVo.getId());
        newOrder.setGoodsPrice(goodsVo.getSeckillPrice());
        newOrder.setUserId(seckillUser.getId());
        newOrder.setDeliveryAddrId(0L);
        newOrder.setOrderChannel(1);
        newOrder.setPayDate(new Date());
        //TODO status用枚举类型
        newOrder.setStatus(0);
        orderDAO.insertOrderInfo(newOrder);
        //seckillOrder
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(newOrder.getId());
        seckillOrder.setUserId(seckillUser.getId());
        int ret = orderDAO.insertSeckillOrder(seckillOrder);
        if (ret <= 0){
            throw new GlobalException(CodeMsg.SECKILL_REPEAT);
        }
        //把seckillOrder写入缓存
        redisService.set(OrderKey.getSeckillOrderByUidGid,
                seckillUser.getId()+"_"+goodsVo.getId(),seckillOrder);
        return newOrder;
    }
}
