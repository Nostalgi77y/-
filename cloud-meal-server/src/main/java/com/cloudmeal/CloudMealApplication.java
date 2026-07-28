package com.cloudmeal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@MapperScan("com.cloudmeal.**.mapper")
@SpringBootApplication
public class CloudMealApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudMealApplication.class, args);
    }
}
