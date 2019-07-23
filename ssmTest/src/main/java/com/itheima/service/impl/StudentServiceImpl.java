package com.itheima.service.impl;

import com.itheima.dao.StudentDao;
import com.itheima.domain.Student;
import com.itheima.service.StudentService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentDao studentDao;
    @Override
    public List<Student> findAll() { ;
        return studentDao.findAll();
    }

    @Override
    public void add(Student student) {
        studentDao.add(student);
    }

    @Override
    public Student findOne(Integer id) {
        return studentDao.findOne(id);
    }

    @Override
    public void update(Student student) {
        studentDao.update(student);
    }

    @Override
    public void del(Integer[] ids) {
        studentDao.del(ids);
    }
}


