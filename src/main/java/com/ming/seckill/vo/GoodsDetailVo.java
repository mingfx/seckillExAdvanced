package com.ming.seckill.vo;

import com.ming.seckill.domain.SeckillUser;

public class GoodsDetailVo {
	private int seckillStatus = 0;
	private int remainSeconds = 0;
	private SeckillGoodsVo goods ;
	private SeckillUser user;

	public int getSeckillStatus() {
		return seckillStatus;
	}

	public void setSeckillStatus(int seckillStatus) {
		this.seckillStatus = seckillStatus;
	}

	public int getRemainSeconds() {
		return remainSeconds;
	}

	public void setRemainSeconds(int remainSeconds) {
		this.remainSeconds = remainSeconds;
	}

	public SeckillGoodsVo getGoods() {
		return goods;
	}

	public void setGoods(SeckillGoodsVo goods) {
		this.goods = goods;
	}

	public SeckillUser getUser() {
		return user;
	}

	public void setUser(SeckillUser user) {
		this.user = user;
	}
}
