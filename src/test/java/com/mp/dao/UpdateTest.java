package com.mp.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.LambdaUpdateChainWrapper;
import com.mp.dao.UserMapper;
import com.mp.entity.User;
import com.sun.xml.internal.fastinfoset.stax.factory.StAXOutputFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateTest {
    @Autowired
    private UserMapper userMapper;

    //DEBUG==>  Preparing: UPDATE mp_user SET name=?, age=? WHERE user_id=?
    //DEBUG==> Parameters: 王天风2(String), 10(Integer), 2(Long)
    @Test
    public void updateById(){
        User user = new User();
        user.setUserId(2l);
        user.setName("王天风2");
        user.setAge(10);
        int rows = userMapper.updateById(user);
        System.out.println("rows:"+rows);
    }

    //DEBUG==>  Preparing: UPDATE mp_user SET age=? WHERE name = ? AND age = ?
    //DEBUG==> Parameters: 12(Integer), 刘红雨(String), 32(Integer)
    @Test
    public void update1(){
        User user = new User();
        user.setAge(12);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("name","刘红雨").eq("age",32);
                            //第一参数中的属性不为null是会set，第二个参数中的是作为where条件
        int rows = userMapper.update(user,updateWrapper);
        System.out.println("rows:"+rows);
    }

    @Test
    public void update2(){
        User user = new User();
        user.setAge(12);
        //注意：若把user传入构造其中，而updateWrapper又在下面写了，在更新条件中会出现相同的age如：where age=12 and name = "刘红雨" and age = 32
        // UpdateWrapper<User> updateWrapper = new UpdateWrapper(user);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("name","刘红雨").eq("age",32);
                            //第一参数中的属性不为null是会set，第二个参数中的是作为where条件
        int rows = userMapper.update(user,updateWrapper);
        System.out.println("rows:"+rows);
    }

    @Test
    public void update3(){
        //更新用UpdateWrapper不是用LambdaQueryWrapper
        UpdateWrapper<User> updateWrapper = new UpdateWrapper();
        //只更新少量字段时不用实体，直接用set属性就可以了
        updateWrapper.eq("name","刘红雨").eq("age",12).set("age",13).set("email","lhy@qq.com");
        int rows = userMapper.update(null,updateWrapper);
        System.out.println("rows:"+rows);
    }

    @Test
    public void updateByWrapperLambda4(){
        LambdaUpdateWrapper<User> lambdaUpdate = Wrappers.<User>lambdaUpdate();
        lambdaUpdate.eq(User::getName,"刘红雨").eq(User::getAge,13).set(User::getAge,31);
        int rows = userMapper.update(null,lambdaUpdate);
        System.out.println("rows = " + rows);
    }

    @Test
    public void updateByWrapperLambda5(){
        Boolean update = new LambdaUpdateChainWrapper<User>(userMapper).eq(User::getAge,13).set(User::getAge,31).update();
        System.out.println("update = " + update);
    }


}
