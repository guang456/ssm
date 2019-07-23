package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 刷新秒杀商品
 */
@Component
public class seckillTesk {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

@Scheduled(cron ="0 * * * * ?")
    public void refreshSeckillGoods(){
    System.out.println("执行了秒杀商品增量执行了任务调度"+new Date());


    //查询所有秒杀商品的键集合
    List goodsIdList  =new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys()) ;
    System.out.println(goodsIdList);
    //查询正在秒杀的商品列表
    TbSeckillGoodsExample example=new TbSeckillGoodsExample();
    TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
    criteria.andStatusEqualTo("1");
    criteria.andStockCountGreaterThan(0);
     criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
    criteria.andEndTimeGreaterThanOrEqualTo(new Date());//结束时间大于当前时间
    if (goodsIdList.size()>0){
        criteria.andIdNotIn(goodsIdList);//排除缓存中已经有的商品id集合
    }
    criteria.andIdNotIn(goodsIdList);//排除缓存中已经有的商品id集合

    List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);

    //放入缓存
    for (TbSeckillGoods seckillGoods : seckillGoodsList) {
        redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(),seckillGoods);
        System.out.println("增加商品id"+seckillGoods.getId());
    }
    System.out.println("===================");
        }

    @Scheduled(cron ="* * * * * ?")
        public void removeSeckillGoods(){

    //查询出缓存中的数据,扫描每条记录判断时间,如果当前时间超过了戒指时间移除此记录
        List<TbSeckillGoods>seckillGoodsList= redisTemplate.boundHashOps("seckillGoods").values();
        System.out.println("执行了清楚秒杀商品的任务");

        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            if (seckillGoods.getEndTime().getTime()<  new Date().getTime()){
                //同步到数据库
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);

                //清楚缓存
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
                System.out.println("秒杀商品"+seckillGoods.getId()+"已经国企");
            }
        }

        System.out.println("执行了清楚秒杀商品的任务.................");
    }
}
