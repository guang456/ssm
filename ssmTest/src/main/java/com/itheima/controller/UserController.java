package com.itheima.controller;

import com.itheima.domain.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    HttpServletRequest request;
    @RequestMapping("login")
    public String login(User user){
        User list = userService.findOne(user);
        if (list!=null){
            request.getSession().setAttribute("user",list);
            return "redirect:/student/findAll.do";
        }else {
            request.setAttribute("msg","用户名密码错误");
            return "login";
        }

    }

}
