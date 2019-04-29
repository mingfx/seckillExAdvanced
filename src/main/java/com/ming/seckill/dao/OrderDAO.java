package com.ming.seckill.dao;

import com.ming.seckill.domain.OrderInfo;
import com.ming.seckill.domain.SeckillOrder;
import com.ming.seckill.domain.SeckillUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDAO {

    @Select("select * from seckill_order where user_id= #{userId} and goods_id= #{goodsId}")
    SeckillOrder getSeckillOrderByUserIdGoodsId(@Param("userId") long userId,
                                                @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date) values(#{userId},"
            + " #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insertOrderInfo(OrderInfo newOrder);

    @Insert("insert ignore into seckill_order(user_id, order_id, goods_id) values(#{userId},#{orderId},#{goodsId})")
    int insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("select * from order_info where id= #{orderId}")
    OrderInfo getOrderInfoByOrderId(@Param("orderId") long orderId);

    @Select("SELECT * from order_info where user_id = #{userId}")
    List<OrderInfo> getOrderInfoListByUserId(@Param("userId") long userId);
}
