package com.cloudmeal.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class MybatisConfig {
    @Bean
    MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override public void insertFill(MetaObject metaObject) {
                strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
                strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
            }
            @Override public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
