package com.mp.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mp.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class serviceTest {
    @Autowired
    private UserService userService;
    @Test
    public void getOne(){
        //第二个参数为true时得到0或1条数据不报错，超过了1条报错，若第二个参数为false时，不管得到多少跳都不会报错，
        // 若使用的是只有一个参数的getOne，相当于第二个参数为true
        User age = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getAge, 180).or().eq(User::getAge, 310));
        System.out.println("age = " + age);
    }

    @Test
    public void Batch(){
        User user1 = new User();
        user1.setName("a");
        user1.setAge(10);
        User user2 = new User();
        user2.setName("ab");
        user2.setAge(20);
        List<User> users = Arrays.asList(user1, user2);
        //批量进行更新或插入操作， saveOrUpdateBatch没有第二参数是默认是1000条 循环预处理 解决内存泄漏jdbc连接超时等
        boolean b = userService.saveOrUpdateBatch(users);
        System.out.println(b);
    }

    @Test
    public void chain(){
        //lambda查询
        List<User> userList = userService.lambdaQuery().gt(User::getAge, 23).like(User::getName, "yu").list();
        userList.forEach(System.out::println);
    }

    @Test
    public void chain1(){
        //lambda更新
        //lambdaUpdate内部 new LambdaUpdateChainWrapper(this.getBaseMapper())
        boolean b = userService.lambdaUpdate().gt(User::getAge, 23).set(User::getName, "yu").update();
        System.out.println(b);
    }
    @Test
    public void chain2(){
        //lambda更新有remove()可以删除
        //lambdaUpdate内部 new LambdaUpdateChainWrapper(this.getBaseMapper())
        boolean b = userService.lambdaUpdate().eq(User::getAge, 20).set(User::getName, "a").remove();
        System.out.println(b);
    }


}
