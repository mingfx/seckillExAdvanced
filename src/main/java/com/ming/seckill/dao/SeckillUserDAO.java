package com.ming.seckill.dao;

import com.ming.seckill.domain.SeckillUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SeckillUserDAO {

    @Select("select * from seckill_user where id = #{id}")
    SeckillUser getById(@Param("id") long id);

    @Insert("insert into seckill_user(id,nickname,password) values(#{id},#{nickname},#{password})")
    int insert(SeckillUser user);

    @Update("update seckill_user set password = #{password} where id = #{id}")
    void update(SeckillUser seckillUser);
}
