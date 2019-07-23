package com.itheima.controller;


import com.itheima.domain.Student;
import com.itheima.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("student")
public class StudentController {
   @Autowired
    StudentService studentService;
    @RequestMapping("findAll")
    public ModelAndView findAll(){
        ModelAndView modelAndView=new ModelAndView();
        List<Student> list = studentService.findAll();
        modelAndView.addObject("list",list);
        modelAndView.setViewName("list");
        return modelAndView;
    }

    @RequestMapping("add")
    public String add(Student student){
        studentService.add(student);
        return "redirect:/student/findAll.do";
    }

    @RequestMapping("findOne")
    public String findOne(Integer id, Model model){
        Student student = studentService.findOne(id);
        model.addAttribute("stu",student);
        return "update";
    }


    @RequestMapping("update")
    public String update(Student student){
        studentService.update(student);
        return "redirect:/student/findAll.do";
    }

    @RequestMapping("del")
    public String del(Integer[] ids){
       studentService.del(ids);
       return "redirect:/student/findAll.do";
    }
}
