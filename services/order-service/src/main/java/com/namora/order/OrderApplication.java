package com.namora.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class OrderApplication {
    static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
