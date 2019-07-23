package com.itheima.service;

import com.itheima.domain.Student;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudentService {
    public List<Student> findAll();
//添加
    public void add(Student student);

    //修改
    public Student findOne(Integer id);
    public void update(Student student);

    //删除
    public void del(Integer[]ids);
}
