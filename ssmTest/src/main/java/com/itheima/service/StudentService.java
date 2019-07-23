package com.itheima.service;

import com.itheima.domain.Student;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentService {
    public List<Student> findAll();
    public void add(Student student);


    public Student findOne(Integer id);

    public void update(Student student);


    public void del(Integer[]ids);
}
