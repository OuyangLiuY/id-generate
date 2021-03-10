package com.generate.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(value = "com.generate.core.segment",basePackages = {"com.generate.common"})
@MapperScan(value = {"com.generate.core.segment.db.mapper"})
@EnableDiscoveryClient
public class IdGenerateApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdGenerateApplication.class,args);
    }
}
