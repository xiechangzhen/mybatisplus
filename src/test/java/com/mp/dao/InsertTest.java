package com.mp.dao;

import com.mp.dao.UserMapper;
import com.mp.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 插入数据
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void insert(){
        User user = new User();
        user.setName("张1");
        user.setAge(20);
        user.setManagerId(2L);
        user.setCreateTime(LocalDateTime.now());
        user.setRemark("beizhu");
        int rows = userMapper.insert(user);
        System.out.println("rows:"+rows);
    }
}
