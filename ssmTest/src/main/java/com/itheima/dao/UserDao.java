package com.itheima.dao;

import com.itheima.domain.User;
import org.apache.ibatis.annotations.Select;

public interface UserDao {
@Select("select * from t_user where name=#{name} and password=#{password}")
    public User findOne(User user);
}
