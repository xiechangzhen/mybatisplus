package com.mp.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mp.dao.UserMapper;
import com.mp.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeleteTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void deleteById(){
        int rows = userMapper.deleteById(1169866890630193154L);
        System.out.println("rows = " + rows);
    }


    @Test
    public void deleteByMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","刘红雨");
        map.put("age",31);
        int rows = userMapper.deleteByMap(map);
        System.out.println("rows = " + rows);
    }

    @Test
    public void deleteBatchIds(){
        int rows = userMapper.deleteBatchIds(Arrays.asList(40L,30L));
        System.out.println("rows = " + rows);
    }

    @Test
    public void deleteByWrapper(){
        LambdaQueryWrapper<User> lambdaQueryWrapper = Wrappers.<User>lambdaQuery();
        lambdaQueryWrapper.eq(User::getAge,2).or().gt(User::getAge,100);
        int delete = userMapper.delete(lambdaQueryWrapper);
        System.out.println("delete = " + delete);
    }

}