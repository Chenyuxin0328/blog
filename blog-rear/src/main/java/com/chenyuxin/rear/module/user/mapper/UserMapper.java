package com.chenyuxin.rear.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenyuxin.rear.module.user.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 可扩展自定义 SQL 方法
}
