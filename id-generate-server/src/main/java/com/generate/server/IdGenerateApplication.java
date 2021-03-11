package com.generate.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.generate.common","com.generate.core.segment","com.generate.server"})
@MapperScan(value = {"com.generate.core.segment.database.mapper"})
@EnableSwagger2
public class IdGenerateApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdGenerateApplication.class,args);
    }
}
