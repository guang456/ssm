package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//更新基本表的数据
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//更新扩展表的数据
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//删除原有的sku列表数据
		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);

		//插入新的sku列表数据
		saveItemList(goods);//插入sku商品数据

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods=new Goods();

		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);

		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);

		//读取sku列表
		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList );
		return goods;//goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();//指定条件为未逻辑删除记录
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				/*criteria.andSellerIdLike("%"+goods.getSellerId()+"%");*/
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
	}


	//添加商品
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Override
	public void add(Goods goods) {
		//设置未申请的状态为0
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getGoods());
		//设置ID
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		//擦汗如商品扩展属性
		goodsDescMapper.insert(goods.getGoodsDesc());
		saveItemList(goods);//插入sku商品数据




	}
	private void setItemValues(TbItem item,Goods goods){
		//商品分类
		item.setCategoryid(goods.getGoods().getCategory3Id());//三级分类id
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//更新日期
		item.setGoodsId(goods.getGoods().getId());//商品id
		item.setSellerId(goods.getGoods().getSellerId());//商家id
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		//品牌名成
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		//商家店铺名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		//图片
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (imageList.size()>0){
			item.setImage((String) imageList.get(0).get("url"));
		}
	}

	private void saveItemList(Goods goods){
		if ("1".equals(goods.getGoods().getIsEnableSpec())){
			for (TbItem item:goods.getItemList()){
				String title=goods.getGoods().getGoodsName();//spu名称
				Map<String,Object> map=JSON.parseObject(item.getSpec());
				for (String key:map.keySet()){
					title+=""+map.get(key);
				}
				item.setTitle(title);

				setItemValues(item,goods);
				itemMapper.insert(item);
			}
		}else {
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//标题
			item.setPrice(goods.getGoods().getPrice());//价格
			item.setNum(9999);//固定的库存数量
			item.setStatus("1");//状态
			item.setIsDefault("1");

			//掉方法
			setItemValues(item,goods);
			itemMapper.insert(item);
		}
	}

//==============================================================================================
	//根据spuid查询sku列表
@Override
public List<TbItem> findItemListByGoodsId(Long[] goodsIds, String status) {
	TbItemExample example=new TbItemExample();
	com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
	criteria.andGoodsIdIn(Arrays.asList(goodsIds));
	criteria.andStatusEqualTo(status);
	return itemMapper.selectByExample(example);
}



};
