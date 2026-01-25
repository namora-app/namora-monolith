package com.namora.order.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DotenvProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            String userDir = System.getProperty("user.dir");
            System.out.println(userDir);
            File envFile = new File(userDir, ".env");
            if (!envFile.exists()) return;
            Dotenv dotenv = Dotenv
                    .configure()
                    .directory(userDir)
                    .filename(".env")
                    .ignoreIfMalformed()
                    .load();

            Map<String, Object> map = new HashMap<>();
            dotenv.entries().forEach(entry -> map.put(entry.getKey(), entry.getValue()));
            if (!map.isEmpty()) environment.getPropertySources().addFirst(new MapPropertySource("dotenv", map));
        } catch (Exception _) {

        }
    }
}