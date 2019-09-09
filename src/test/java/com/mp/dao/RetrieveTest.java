package com.mp.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.mp.dao.UserMapper;
import com.mp.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询数据
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RetrieveTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void selectById(){
        User user = userMapper.selectById(2);
        System.out.println("user:"+user);
    }

    @Test //多个id查询
    public void selectBatchIds(){
        List<Long> idList = Arrays.asList(2L, 3L);
        List<User> users = userMapper.selectBatchIds(idList);
        users.forEach(System.out::println);
    }

    @Test //根据列名查询 它生成的sql是用and连接各列的
    public void selectByMap(){
        Map<String,Object> columnMap = new HashMap<>();
        //注意：map中的String是数据库中的列，不是实体中的名字
        columnMap.put("name","王天风");
        columnMap.put("age",25);
        List<User> users = userMapper.selectByMap(columnMap);
        users.forEach(System.out::println);
    }

    /**
     * 1.查询名字中包含张并且年龄小于20
     * name like '%张%' and  age<20
     */
    @Test //根据条件构造器查询
    public void selectByWrapper(){
        //创建queryWrapper方式一
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //创建queryWrapper方式二
       //QueryWrapper<User> queryWrapper = Wrappers.<User>query();
        queryWrapper.like("name","张").lt("age",20);
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 2.名字中包含张并且年龄大于等于20且小于等于40并且email不为空
     * name like '%张%' and age between 20 and 40 and email is not null
     */
    @Test //根据条件构造器查询
    public void selectByWrapper2(){
        //创建queryWrapper方式一
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name","张").between("age",20,40).isNotNull("email");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 3.名字中包含张并且年龄大于等于25,按照年龄降序排列，年龄相同按照id升序排列
     * name like '张%' or age>=40 order by age desc,id asc
     */
    @Test //根据条件构造器查询
    public void selectByWrapper3(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("name","张").or().ge("age",25).orderByDesc("age")
                .orderByAsc("user_id");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 4.创建日期为2019年2月14日并且直属上级的名字为王姓
     * date_format(create_time,'%Y-%m-%d')and manager_id in (select id from user where name like '王%')
     */
    @Test //根据条件构造器查询
    public void selectByWrapper4(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        /**
         * apply("date_format(create_time,'%Y-%m-%d') = '2019-02-14  or true '")  有sql注入风险
         * apply("date_format(create_time,'%Y-%m-%d') = {0}","2019-02-14")  {0}表示第一个参数，这种方式没有sql注入风险没有sql注入风险
         */
        queryWrapper.apply("date_format(create_time,'%Y-%m-%d') = {0}","2019-02-14")
                .inSql("manager_id","select user_id from mp_user where name like '王%'");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 5.名字中包含王并且（年龄小于40或邮箱不为空）
     * name like '王%' and( age<40 or email is not null )
     */
    @Test
    public void selectByWrapper5(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("name","王").and(wq->wq.lt("age",40).or().isNotNull("email"));
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 6.名字中为王姓或者（年龄小于40并且年龄大于20并且邮箱不为空）
     * name like '王%' or( age<40 and age>20 and email is not null )
     */
    @Test
    public void selectByWrapper6(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("name","王").or(wq->wq.lt("age",40).gt("age",20).isNotNull("email"));
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 7.（年龄小于40或者邮箱不为空）并且名字为王姓
     *  ( age<40 and age>20 or email is not null ) and like '王%'
     *  and的优先级高于or
     */
    @Test
    public void selectByWrapper7(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 因为是第一句，又要使用函数所以使用nested
        queryWrapper.nested(wq->wq.lt("age",40).gt("age",20).isNotNull("email")).likeRight("name","王");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 8.年龄为30,31,34,35
     *  age in(30,31,34,35)
     */
    @Test
    public void selectByWrapper8(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("age",Arrays.asList(30,31,34,35));
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 9.年龄为30,31,34,35
     *  limit 1
     *  慎用有sql注入风险,如果limit不是用户提交的数据，可用，
     */
    @Test
    public void selectByWrapper9(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("age",Arrays.asList(30,31,34,35)).last("limit 1");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    //**********************************二
    /**
     * 1.1查询名字中包含张并且年龄小于20
     * name like '%张%' and  age<20
     * 只查询user_id ， name
     */
    @Test //根据条件构造器查询
    public void selectByWrapperSupper1(){
        //创建queryWrapper方式一
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //select写在前后都可以
        queryWrapper.select("user_id","name").like("name","张").lt("age",20);
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * .查询名字中包含张并且年龄小于20
     * name like '%张%' and  age<20
     * 只查询user_id ， name
     */
    @Test //根据条件构造器查询
    public void selectByWrapperSupper2(){
        //创建queryWrapper方式一
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //create_time和manager_id这两个字段不输出其他字段都输出
        queryWrapper.like("name","张").lt("age",20)
                .select(User.class,info->!info.getColumn().equals("create_time")&&
                        !info.getColumn().equals("manager_id"));
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    @Test //条件构造器中condition的作用
    public void testCondition(){
        String name = "王";
        String email = "";
        condition(name,email);
    }
    private void condition(String name,String email){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
/*        if(StringUtils.isNotEmpty(name)){
            queryWrapper.like("name",name);
        }
        if(StringUtils.isNotEmpty(email)){
            queryWrapper.like("email",email);
        }*/

    //效果和上面一样，StringUtils.isNotEmpty(name)为true是才会加入这个条件
        queryWrapper.like(StringUtils.isNotEmpty(name),"name",name)
        .like(StringUtils.isNotEmpty(email),"email",email);
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * .查询名字中包含张并且年龄小于20
     * name like '%张%' and  age<20
     * 只查询user_id ， name
     */
    @Test
    public void selectByWrapperEntity(){
        User whereUser = new User();
        whereUser.setName("刘红雨");
        whereUser.setAge(35);
        //使用实体传入条件，当属性实体属性不为空是会默认以and连接各个属性的值，
        // 慎用，若下面自己单独加的条件也都会生成（重复）
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>(whereUser);
        //queryWrapper.like("name","雨").lt("age",40)
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * .查询名字中包含张并且年龄小于20
     * name like '%张%' and  age<20
     * 只查询user_id ， name
     */
    @Test
    public void selectByWrapperAllEq(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("name","王天风");
        params.put("age",null);
        //where name = "王天风" and age is null
        //queryWrapper.allEq(params);
        //若第二个参数为false，params中key对应的value为null则会忽略掉， where name = "王天风"
        // queryWrapper.allEq(params,false);
        //过滤key为name的，v可以其实也可以过滤， where age is null
        queryWrapper.allEq((k,v)->!k.equals("name"),params);
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    //******3-10其他使用条件构造器的方法

    /**
     * selectMaps应用场景
     * 1.当selectList则会返回整实体类，没有查询的都为null，不美观
     * 2. 11.按照直属上级分组，查询每组的平均年龄、最大年龄、最小年龄。
     * 并且只取年龄总和小于500的组。
     * select avg(age) age_age,min(age) min_age,max(age) max_age
     * from user
     * group by manager_id
     * having sum(age)<500;
     */
    @Test   /////////////////////////////////////
    public void selectByWrapperMaps(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        //queryWrapper.select("id","name").like("name","雨").lt("age",40);
        //返回的不是实体中的列，是聚函数等
        queryWrapper.select("avg(age) age_age","min(age) min_age","max(age) max_age")
                .groupBy("manager_id").having("sum(age)<{0}",500);
        List<Map<String, Object>> mapList = userMapper.selectMaps(queryWrapper);
        mapList.forEach(System.out::println);
    }

    @Test
    public void selectObjs(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.select("user_id","name").like("name","雨").lt("age",40);
        //只会返回查询结果中的第一列，应用场景为返回结果只有一列时。
        List<Object> obj = userMapper.selectObjs(queryWrapper);
        obj.forEach(System.out::println);
    }

    @Test
    public void selectCount(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.select("user_id","name").like("name","雨").lt("age",40);
        //返回查询结果总条数。
        Integer count = userMapper.selectCount(queryWrapper);
        System.out.println("总记录数："+ count);
    }

    @Test
    public void selectOne(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.select("user_id","name").lt("age",40);
        //返回查询结果总条数只能为一条或没有，其他报错
        User user = userMapper.selectOne(queryWrapper);
        System.out.println("user："+ user);
    }

    //查询 Lambda条件构造器 User::getName主要是用于防止写错，写错编译会提示
    @Test
    public void selectLambda1(){
        //LambdaQueryWrapper创建的3中方式
        //LambdaQueryWrapper<User> lambda = new QueryWrapper<User>().lambda();
        //LambdaQueryWrapper<User> lambda = new LambdaQueryWrapper<User>();
        LambdaQueryWrapper<User> lambda = Wrappers.<User>lambdaQuery();
        lambda.like(User::getName,"雨").lt(User::getAge,40);
        //编译后是 where name like '%雨%' and age < 40;
        List<User> userList = userMapper.selectList(lambda);
        userList.forEach(System.out::println);
    }

    /**
     *  5.名字中包含王并且（年龄小于40或邮箱不为空）
     *  name like '王%' and (age<40 or email is not null)
     */
    @Test
    public void selectLambda2(){
        LambdaQueryWrapper<User> lambda = Wrappers.<User>lambdaQuery();
        lambda.likeRight(User::getName,"王")
                .and(lw->lw.lt(User::getAge,40).or().isNotNull(User::getEmail));
        List<User> userList = userMapper.selectList(lambda);
        userList.forEach(System.out::println);
    }

    //list其实也是调用selectList方法
    @Test       //////////////////////////
    public void selectLambda3(){
        List<User> userList = new LambdaQueryChainWrapper<User>(userMapper).like(User::getName, "雨").ge(User::getAge, 20).list();
        userList.forEach(System.out::println);
    }

/*  调用自己中dao中定义的方法
    @Select("select * from user ${ew.customSqlSegment}")
    List<User> selectAll(@Param(Constants.WRAPPER)Wrapper<User> wrapper)
    */
    @Test
    public void selectMy(){
        LambdaQueryWrapper<User> lambda = Wrappers.<User>lambdaQuery();
        lambda.likeRight(User::getName,"王")
                .and(lw->lw.lt(User::getAge,40).or().isNotNull(User::getEmail));
        List<User> userList = userMapper.selectAll(lambda);
        userList.forEach(System.out::println);
    }

    /**
     * 不使用注解，使用xml
     * 在yml中添加下面-com/mp/mapper/*的配置
     *
     */
//    mybatis-plus:
//    mapper-locations: classpath:mapper/**

/*    <?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mp.dao.UserMapper">
    <select id="selectAll" resultType="com.mp.entity.User">
        select * from mp_user ${ew.customSqlSegment}
    </select>
    <select id="selectUserPage" resultType="com.mp.entity.User">
        select * from mp_user ${ew.customSqlSegment}
    </select>
</mapper>*/

    //分页
    @Test
    public void selectPage(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.ge("age",26).select("name");
        //queryWrapper.ge("age",26);
        //    二个参是会查询总记录数      当前页数  要显示的条数
        //Page<User> userPage = new Page<User>(1, 2);
        //    三个参时若第三个为false则不会查询总记录数 ，为true是会查询总记录数。
        Page<User> userPage = new Page<User>(1, 4,false);
//        IPage<User> iPage = userMapper.selectPage(userPage,queryWrapper);
//        System.out.println("总页数：" + iPage.getPages());
//        System.out.println("总记录数："+iPage.getTotal());
//        List<User> userList = iPage.getRecords();
//        userList.forEach(System.out::println);

        IPage<Map<String,Object>> iPage = userMapper.selectMapsPage(userPage,queryWrapper);
        System.out.println("总页数：" + iPage.getPages());
        System.out.println("总记录数："+iPage.getTotal());
        List<Map<String,Object>> userList = iPage.getRecords();
        userList.forEach(System.out::println);
    }

    //自定义方法分页
    @Test
    public void selectMYPage() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.ge("age", 26);
        Page<User> userPage = new Page<User>(1, 2,false);
        IPage<User> iPage = userMapper.selectUserPage(userPage,queryWrapper);
        System.out.println("总页数：" + iPage.getPages());
        System.out.println("总记录数："+iPage.getTotal());
        List<User> userList = iPage.getRecords();
        userList.forEach(System.out::println);
    }

}
