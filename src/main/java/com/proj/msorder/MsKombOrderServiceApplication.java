package com.proj.msorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsKombOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsKombOrderServiceApplication.class, args);
    }

}
