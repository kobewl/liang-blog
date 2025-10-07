package com.liang.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liang.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {}
