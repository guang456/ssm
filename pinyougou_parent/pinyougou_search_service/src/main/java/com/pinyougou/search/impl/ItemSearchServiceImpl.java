package com.pinyougou.search.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
private SolrTemplate solrTemplate;

    @Override
    public Map<String,Object> search(Map searchMap) {
        //创建一个集合
        Map<String,Object> map=new HashMap<String, Object>();

        //空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));//关键字去掉空格
        //1,查询列表
        map.putAll(searchList(searchMap));

        //2;分组查询商品分类列表
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //3查询规格和品牌列表
        String category = (String) searchMap.get("category");
                if (!category.equals("")){//如果规格选项不为空就以用户选择的为主
                    map.putAll(searchBrandAndSpecList(category));
                }else {
                    if (categoryList.size()>0){//如果选项为空就以第一个规格为主
                        map.putAll(searchBrandAndSpecList(categoryList.get(0)));
                    }
                }

        return map;
    }



    //=================================================================================================================================
        /*
        方法一:查询列表以及高亮显示
        * */
         private Map searchList (Map searchMap) {
        Map<String,Object> map=new HashMap<String, Object>();
        //高亮显示
        HighlightQuery query = new SimpleHighlightQuery();
        //构建高亮选项部分
        HighlightOptions highlightQuery = new HighlightOptions().addField("item_title");//高亮的部分
        highlightQuery.setSimplePrefix("<em style='color:red'>");//前缀
        highlightQuery.setSimplePostfix("</em>");//后缀
        query.setHighlightOptions(highlightQuery);//为查询对象设置高亮选项


        //1.1关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2按商品分类过滤
            if (!"".equals(searchMap.get("category"))){//只有用户选择了分类才能赛选
                 FilterQuery filterQuery=new SimpleFacetQuery();
                 Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
                 filterQuery.addCriteria(filterCriteria);
                 query.addFilterQuery(filterQuery);
             }
   //==================================================================================================================================
             //1.3按品牌分类过滤
             if (!"".equals(searchMap.get("brand"))){//只有用户选择了分类才能赛选
                 FilterQuery filterQuery=new SimpleFacetQuery();
                 Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
                 filterQuery.addCriteria(filterCriteria);
                 query.addFilterQuery(filterQuery);
             }
//===============================================================================================================================
                //1.4根据规格过滤
             if(searchMap.get("spec")!=null){
                 Map<String,String> specMap= (Map) searchMap.get("spec");
                 for(String key:specMap.keySet() ){
                     Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
                     FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                     query.addFilterQuery(filterQuery);
                 }
             }

//==============================================================================================================================

             //1.5按价钱过滤


             if (!"".equals(searchMap.get("price"))) {
                /* String priceStr = (String) searchMap.get("price");//500-100
                 String[] price = priceStr.split("-");//将价钱切分*/

                 String[] price = ((String) searchMap.get("price")).split("-");

                 if (!price[0].equals("0")) {//如果最低价格不等于0
                     FilterQuery filterQuery = new SimpleFacetQuery();
                     Criteria filterCriteria = new Criteria("item_price").greaterThanEqual((price[0]));//大于等于
                     filterQuery.addCriteria(filterCriteria);
                     query.addFilterQuery(filterQuery);
                 }
                 if (!price[0].equals("*")) {//如果最高的价格不等于*
                     FilterQuery filterQuery = new SimpleFacetQuery();
                     Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);//小于等于
                     filterQuery.addCriteria(filterCriteria);
                     query.addFilterQuery(filterQuery);


                 }

             }

//==============================================================================================================================
            //1.6 分页

             Integer pageNum = (Integer) searchMap.get("pageNum");//获取当前页码
             if (pageNum==null){
                 pageNum=1;//默认第一页
             }
             Integer pageSize = (Integer) searchMap.get("pageSize");//获取每页显示条数
             if (pageSize==null){
                 pageSize=20;
             }
             query.setOffset((pageNum-1)*pageSize);//开始的索引
             query.setRows(pageSize);//每页记录数



//==============================================================================================================================
             //1.7 按价格排序 ASC升序
             String sortValue = (String) searchMap.get("sort");//排序   asc升序 desc降序
             String sortField = (String) searchMap.get("sortField");//排序的字段
             if (sortValue!=null &&!sortValue.equals("")){
                 if (sortValue.equals("ASC")){
                     Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
                     query.addSort(sort);
                 }
                 if (sortValue.equals("DESC")){
                     Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
                     query.addSort(sort);
                 }
             }




//====================获取高亮结果集=======================================================================================
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //高亮入口集合(每条记录的高亮入口)
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : entryList) {
            //获取高亮列表
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();
            /*
            for (HighlightEntry.Highlight hi : highlights) {
                List<String> sns = hi.getSnipplets();
            }
            */
            if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
                TbItem item = entry.getEntity();
                item.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",page.getContent());
        map.put("totalPages",page.getTotalPages());//总页shu
        map.put("total",page.getTotalElements());//总记录数
        return map;
    }
    //===================================================================================================================================

    /*
    * 方法二:分组查询(查询商品分类)
     * */
    private List<String> searchCategoryList(Map searchMap){
            List<String> list=new ArrayList();

        Query query=new SimpleQuery("*:*");
        //关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
            //设置分组选项
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");//相当于sql中的group by ,可以多个选项
        query.setGroupOptions(groupOptions);
        //获得分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //分组的结果对象
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //获取分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //货物分组如露对象
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : entryList) {
         list.add( entry.getGroupValue());//将分组的结果添加到返回值中

        }
        return list;
    }

    //===========================================================================================================================
    @Autowired
    private RedisTemplate redisTemplate;
        /*
        *根据商品你分类名称查询品牌和规格列表
        * */
    private Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        //根据商品分类取到模板id
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (templateId!=null){
            //根据模板id获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList",brandList);

            //根据模板id获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList",specList);
        }
       return map;
    }

    //=======================================================================================================
    /*
    * 导入列表
    * */
    @Override
    public void importList(List list) {
            solrTemplate.saveBeans(list);
            solrTemplate.commit();
    }

    @Override
    public void deleteByGoodIds(List goodIds) {
       Query query=new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_goodsid").in(goodIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();

    }

}
