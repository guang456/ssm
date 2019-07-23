package com.pinyougou.page.service.impl;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbGoodsMapper goodsMapper;
   @Autowired
   private TbItemCatMapper itemCatMapper;
   @Autowired
   private TbItemMapper itemMapper;
    @Override
    public boolean genItemHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfig.getConfiguration();

        try {
            Template template=configuration.getTemplate("item.ftl");

            //创建你数据模型
            Map dataModel=new HashMap();
            //1商品的主表数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",goods);
            //2商品的扩展表数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc",goodsDesc);
            //3 读取商品分类列表
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            dataModel.put("itemCat1",itemCat1);
            dataModel.put("itemCat2",itemCat2);
            dataModel.put("itemCat3",itemCat3);

            //读取sku列表(tb_item)
            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);//获取spu列表数据id
            criteria.andStatusEqualTo("1");//商品审核通过
            example.setOrderByClause("is_default desc");//按是否为默认字段进行降序排序,母的是让返回的结果第一天为默认的sku;
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList",itemList);

            //乱码
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(pagedir + goodsId + ".html"), "utf-8");

            template.process(dataModel,out);
            out.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for(Long goodsId:goodsIds){
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
