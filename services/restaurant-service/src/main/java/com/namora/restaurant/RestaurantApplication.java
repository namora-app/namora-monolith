package com.namora.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class RestaurantApplication {
    static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
    }
}
