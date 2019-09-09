package com.mp.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mp.dao.UserMapper;
import com.mp.entity.User;
import com.sun.xml.internal.fastinfoset.stax.factory.StAXOutputFactory;
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
public class VRTest {
    /**
     * 实体中要继承Model和SerialVersionUID
     * public class User extends Model<User> {
     * private static final long SerialVersionUID = 1L;
     */
    //VR模式不用userMapper
    @Test
    public void insert(){
        User user = new User();
        user.setName("zhangchao");
        user.setAge(21);
        user.setEmail("zc@qq.com");
        user.setManagerId(2L);
        boolean insert = user.insert();
        System.out.println("insert = " + insert);
    }

    @Test
    public void selectById(){
        User user = new User();
        User userSelect = user.selectById(2L);
        System.out.println("userSelect= " + userSelect);
    }

    @Test
    public void selectById1(){
        User user = new User();
        User userSelect = user.selectById(2L);
        //user和userSelect不一样
        System.out.println(user==userSelect);
        System.out.println("userSelect= " +userSelect);
    }

    @Test
    public void selectById2(){
        User user = new User();
        user.setUserId(2L);
        User userSelect = user.selectById();
        //user和userSelect不一样
        System.out.println(user==userSelect);
        System.out.println("userSelect= " +userSelect);
    }

    @Test
    public void updateById(){
        User user = new User();
        user.setUserId(2L);
        user.setName("zheng");
        boolean updateById = user.updateById();
        System.out.println("updateById = " +updateById);
    }

    /**
     * 会先根据id查询，如果id存在着是更新操作。不存在则会进行插入操作，
     * 如果有user中user有id但是数据库中查不到该id，插入时会以user中id值插入。
     */
    @Test
    public void insertOrUpdate(){
        User user = new User();
        user.setUserId(2L);
        user.setName("zheng");
        boolean updateById = user.insertOrUpdate();
        System.out.println("updateById = " +updateById);
    }

    @Test
    public void deleteById(){
        User user = new User();
        user.setUserId(1169866531773939714L);
        //源码中return null != result && result >= 0; 删除不存在的数据也是删除成功
        boolean updateById = user.deleteById();
        System.out.println("updateById = " +updateById);
    }
}