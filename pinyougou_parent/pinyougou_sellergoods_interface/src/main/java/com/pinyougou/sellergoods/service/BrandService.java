package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 */
public interface BrandService {
    /**
     * 查询所有
     * @return
     */
    public List<TbBrand> findAll1();

    /*
    *品牌分页
    * // pageNum当前页
    *pageSize每页记录数
    * */
    public PageResult findPage(int pageNum,int pageSize);

    /*
    * 添加品牌
    * */
    public void add(TbBrand brand);

    /*
     * 根基 id查询品牌
     * */
    TbBrand findOne(Long id);

    /*
    * 品牌修改
    * */
    public void update(TbBrand brand);

    /*
    * 品牌的删除
    * */
    public void delete(Long []ids);


    /*
     *品牌分页
     * 根据条件查询
     * // pageNum当前页
     *pageSize每页记录数
     * */
    public PageResult findPage(TbBrand brand, int pageNum,int pageSize);

    /*
    * 返回下拉列表
    * */
public  List<Map>selectOptionList();
}
