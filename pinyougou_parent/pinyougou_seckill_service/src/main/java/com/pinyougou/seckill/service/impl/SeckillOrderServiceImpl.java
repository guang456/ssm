package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}





	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private IdWorker idWorker;
	@Override
	public void submitOrder(Long seckillId, String userId) {

		//查询缓存中的商品

		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
		if (seckillGoods==null){
			throw  new RuntimeException("商品不存在");
		}
		if (seckillGoods.getStockCount()<=0){
			throw  new RuntimeException("商品已经被抢完");
		}

		//减少库存
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);//见库存
		redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);
		if (seckillGoods.getStockCount()==0){
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//更新数据库
			redisTemplate.boundHashOps("seckillGoods").delete(seckillId);//清除缓存


			System.out.println("商品同步到数据库");

		}

		//存储秒杀订单
		TbSeckillOrder seckillOrder=new TbSeckillOrder();
		seckillOrder.setId(idWorker.nextId());//ID
		seckillOrder.setSeckillId(seckillId);//秒杀id
		seckillOrder.setMoney(seckillGoods.getCostPrice());//支付金额
		seckillOrder.setUserId(userId);//用户
		seckillOrder.setSellerId(seckillGoods.getSellerId());//商家id
		seckillOrder.setCreateTime(new Date());//创建时间
		seckillOrder.setStatus("0");//状态

		redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder);

		System.out.println("保存订单成功(redis)");

	}

	//查询秒杀订单
	@Override
	public TbSeckillOrder searchOrderFromByUserId(String userId) {

		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	@Override
	public void saveOderFromRedisToDb(String userId, Long orderId, String transactionId) {

		 //从缓存中提取订单
		TbSeckillOrder seckillOrder = searchOrderFromByUserId(userId);
		if (seckillOrder==null){
			throw new RuntimeException("订单不存在");
		}
		if (seckillOrder.getId().longValue()!=orderId.longValue()){
			throw  new RuntimeException("订单号不符");
		}
		//修改订单实体的属性
		seckillOrder.setPayTime(new Date());//支付时间
		seckillOrder.setStatus("1");//支付状态 已经支付
		seckillOrder.setTransactionId(transactionId);//交易流水账号

		//将订单存入数据库
		seckillOrderMapper.insert(seckillOrder);

		//清楚缓存中的订单
		redisTemplate.boundHashOps("seckillOrder").delete(userId);
	}

}
