package com.bocsoft.obss.shiro.mapper;

import com.bocsoft.obss.shiro.entity.UserBean;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends MppBaseMapper<UserBean> {

}
