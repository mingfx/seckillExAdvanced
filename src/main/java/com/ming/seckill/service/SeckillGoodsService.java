package com.ming.seckill.service;

import com.ming.seckill.dao.SeckillGoodsDAO;
import com.ming.seckill.vo.SeckillGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsService {
    @Autowired
    SeckillGoodsDAO seckillGoodsDAO;

    public List<SeckillGoodsVo> getListSeckillGoodsVo(){
        return seckillGoodsDAO.getListSeckillGoodsVo();
    }

    public SeckillGoodsVo getSeckillGoodsVoById(long goodsId){
        return seckillGoodsDAO.getSeckillGoodsVoById(goodsId);
    }

    public boolean reduceStock(SeckillGoodsVo goodsVo){
        return seckillGoodsDAO.reduceStock(goodsVo.getId()) > 0;
    }
}
