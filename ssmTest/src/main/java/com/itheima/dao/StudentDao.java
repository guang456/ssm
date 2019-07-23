package com.itheima.dao;

import com.itheima.domain.Student;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface StudentDao {
    public List<Student> findAll();

    //添加
    public void add(Student student);

    //修改
    @Select("select * from t_student where id=#{id}")
    public Student findOne(Integer id);
    @Select("update  t_student set name=#{name},sex=#{sex},age=#{age},address=#{address} where id=#{id}")
    public void update(Student student);

    ///删除
    public void del(Integer[]ids);
}
