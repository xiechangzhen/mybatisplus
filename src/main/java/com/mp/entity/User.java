package com.mp.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.javassist.SerialVersionUID;

import java.time.LocalDateTime;

@Data
//@EqualsAndHashCode(callSuper = false)
//@TableName("mp_user")  //数据库表明驼峰命名后和不实体类名不一致时，要写上这句
public class User extends Model<User> {
    private static final long SerialVersionUID = 1L;
    //*注意：主键 默认为id 时，可以不用加@TableId，但是不为id时即使和数据库中的名字相同都为userId（user_id）都要写上注解才识别
    //主键策略，和数据库中类型要统一，  如果再yml配置中设置了全局策略，又在这里设置了局部策略，则局部策略优先于全局策略
    //@TableId
    //@TableId(type = IdType.AUTO)和设置为 @TableId(type = IdType.NONE)时一样的，
    @TableId(type = IdType.ID_WORKER) //ID_WORKER雪花算法

    private Long UserId;

    /**
     * 姓名
     * 当以整个实体为传入查询的条件默认是等于各个属性的，要不相等可以写@TableField(condition= SqlCondition.LIKE)等
     *  @TableField(condition= SqlCondition.LIKE)
     *
     * 其他的属性和数据库中的名字对不上时就加上@TableField（）
     *  @TableField("real_name")
     */
    //@TableField(condition= SqlCondition.LIKE)
    private String name;
    //年龄 SqlCondition中没有的可以模仿自己写，%s代表列名，#{%s}代表值
    //TableField(condition="%s&lt;#{%s}")
     //@TableField(strategy = FieldStrategy.NOT_EMPTY) 该属性为null或空字符串也会忽略，进行插入更新等不进行该字段操作
    private Integer age;
    //邮箱
    private String email;
    //直属上级
    private Long managerId;
    //创建时间
    private LocalDateTime createTime;

    //备注
    /**
     * 数据库中没有该字段时
     * 方法一：加上transient 序列化时没用     private transient remark;
     * 方法二：加上static 在加上该属性对应的set get方法   private static remark; 所有对象共用一个；
     * 方法三： 加上@TableField(exist = false) 注解；可以完美解决上面方法中的不足；
     */
    @TableField(exist = false)
    private String remark;

}
