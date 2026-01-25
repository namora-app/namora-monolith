package com.namora.dispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class DispatchApplication {
    static void main(String[] args) {
        SpringApplication.run(DispatchApplication.class, args);
    }
}
