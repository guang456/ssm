package com.itheima.dao;

import com.itheima.domain.Student;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentDao {
    @Select("select * from peoper")
    public List<Student>findAll();

    //添加
    @Select("insert into peoper(id,name,age,sex,classhome,address) values(#{id},#{name},#{age},#{sex},#{classhome},#{address})")
    public void add(Student student);

    //修改
    @Select("select * from peoper where id=#{id}")
    public Student findOne(Integer id);
    @Select("update peoper set name=#{name},age=#{age},sex=#{sex},classhome=#{classhome},address=#{address} where id=#{id}")
    public void update(Student student);

    //删除
    public void del(Integer[]ids);
}
