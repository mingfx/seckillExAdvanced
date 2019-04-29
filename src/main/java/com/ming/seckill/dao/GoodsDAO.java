package com.ming.seckill.dao;

import com.ming.seckill.domain.SeckillUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GoodsDAO {

    @Select("select * from seckill_user where id = #{id}")
    public SeckillUser getById(@Param("id") long id);

    @Insert("insert into seckill_user(id,nickname,password) values(#{id},#{nickname},#{password})")
    public int insert(SeckillUser user);
}
