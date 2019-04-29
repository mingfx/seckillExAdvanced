package com.ming.seckill.dao;

import com.ming.seckill.domain.SeckillGoods;
import com.ming.seckill.domain.SeckillUser;
import com.ming.seckill.vo.SeckillGoodsVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SeckillGoodsDAO {

    @Select("SELECT g.*, sg.stock_count,sg.start_date,sg.end_date,sg.seckill_price from seckill_goods sg LEFT JOIN goods g ON sg.goods_id = g.id WHERE g.id = #{goodsId}")
    public SeckillGoodsVo getSeckillGoodsVoById(@Param("goodsId") long goodsId);

    @Select("SELECT g.*, sg.stock_count,sg.start_date,sg.end_date,sg.seckill_price from seckill_goods sg LEFT JOIN goods g ON sg.goods_id = g.id")
    public List<SeckillGoodsVo> getListSeckillGoodsVo();

    @Update("update seckill_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    public int reduceStock(@Param("goodsId") long goodsId);
}
