package com.ming.seckill.redis;

public class OrderKey extends BasePrefix {
    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static OrderKey getOrderListHtml = new OrderKey(60,"ol");
    public static OrderKey getOrderDetailHtml = new OrderKey(60,"od");
    public static OrderKey getSeckillOrderByUidGid = new OrderKey(0,"soug");

}
