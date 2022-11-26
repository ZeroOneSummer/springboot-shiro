package com.bocsoft.obss.shiro.entity;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * mp自动填充器
 */
@Slf4j
@Component
public class DateHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		log.info("start insert fill...");
		this.setFieldValByName("createDate", LocalDateTime.now(), metaObject);
		this.setFieldValByName("updateDate", LocalDateTime.now(), metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		log.info("start update fill...");
		this.setFieldValByName("updateDate", LocalDateTime.now(), metaObject);
	}
}