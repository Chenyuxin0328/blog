package com.chenyuxin.rear;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RearApplication {

    public static void main(String[] args) {
        SpringApplication.run(RearApplication.class, args);
    }

}
