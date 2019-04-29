package com.ming.seckill.rabbitMQ;

import com.ming.seckill.domain.OrderInfo;
import com.ming.seckill.domain.SeckillOrder;
import com.ming.seckill.domain.SeckillUser;
import com.ming.seckill.exception.GlobalException;
import com.ming.seckill.redis.RedisService;
import com.ming.seckill.redis.SeckillKey;
import com.ming.seckill.result.CodeMsg;
import com.ming.seckill.result.Result;
import com.ming.seckill.service.OrderService;
import com.ming.seckill.service.SeckillGoodsService;
import com.ming.seckill.service.SeckillService;
import com.ming.seckill.vo.SeckillGoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MQReceiver.class);
    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedisService redisService;

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message) {
        logger.info("receive message :"+message);
        SeckillMessage seckillMessage = RedisService.stringToBean(message,SeckillMessage.class);
        SeckillUser seckillUser = seckillMessage.getSeckillUser();
        long goodsId = seckillMessage.getGoodsId();

        //判断库存（从数据库中取准确的库存
        SeckillGoodsVo goodsVo = seckillGoodsService.getSeckillGoodsVoById(goodsId);
        String seckillKey = seckillUser.getId()+"_"+goodsId;
        int stock = goodsVo.getStockCount();
        if (stock <= 0){
            //库存不足，返回失败信息
            updateResultStatus(seckillKey,Result.error(CodeMsg.SECKILL_RUNOUT));
            return;
        }
        //判断是否已经秒杀过了(做了优化，查缓存里的订单）
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(),goodsId);
        if (seckillOrder!=null){
            //重复秒杀，返回失败信息
            updateResultStatus(seckillKey,Result.error(CodeMsg.SECKILL_REPEAT));
            return;
        }
        //秒杀,成功返回订单详情
        try {
            OrderInfo orderInfo = seckillService.seckill(seckillUser,goodsVo);
            if (orderInfo!=null){
                updateResultStatus(seckillKey,Result.success(orderInfo.getId()));
            }
        } catch (GlobalException e) {
            logger.error(e.getCodeMsg().getMsg());
            updateResultStatus(seckillKey,Result.error(e.getCodeMsg()));
        }
    }

    private void updateResultStatus(String seckillKey,Result result){
        redisService.set(SeckillKey.seckillResult,seckillKey,result);
    }

//    /**
//     * 有四种交换机Exchange模式
//     * 最简单的direct模式，指定队列名
//     * @param message
//     */
//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        logger.info("receive message :"+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message){
//        logger.info("receive topic queue1 message :"+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message){
//        logger.info("receive topic queue2 message :"+message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
//    public void receiveHeaders(byte[] message){
//        //header 模式，要接受byte数组
//        logger.info("receive header queue1 message :"+new String(message));
//    }
}
