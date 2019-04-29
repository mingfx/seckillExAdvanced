package com.ming.seckill.vo;

import com.ming.seckill.domain.OrderInfo;

public class OrderDetailVo {
	private SeckillGoodsVo goods;
	private OrderInfo order;

	public SeckillGoodsVo getGoods() {
		return goods;
	}

	public void setGoods(SeckillGoodsVo goods) {
		this.goods = goods;
	}

	public OrderInfo getOrder() {
		return order;
	}

	public void setOrder(OrderInfo order) {
		this.order = order;
	}
}
