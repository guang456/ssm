package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    //查看所有
    public List<TbBrand> findAll1() {
        return brandMapper.selectByExample(null);
    }



    @Override
    //分页查看所有
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);//myBatis的分页,PageHelper分页插件
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }
/*
* 添加品牌
* */
    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }
    /*
     * 根据id查询品牌
     * */
    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
    /*
     * 修改品牌
     * */
    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);//根据主键更新
    }

    /*
     * 删除品牌
     * */
    @Override
    public void delete(Long[] ids) {
        //遍历ids循环调用id
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);//myBatis的分页,PageHelper分页插件

        //创建条件
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();

        if (brand != null) {
            if (brand.getName() != null && brand.getName().length() > 0) {
                criteria.andNameLike("%" + brand.getName() + "%");//模糊查询根据名字
            }
            if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
                criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");//模糊查询根据首字母
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);


        return new PageResult(page.getTotal(), page.getResult());

    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
