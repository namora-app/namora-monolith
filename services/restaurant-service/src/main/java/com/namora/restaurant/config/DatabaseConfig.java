package com.namora.restaurant.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DatabaseConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void enablePostgresExtensions() {
        try {
            entityManager.createNativeQuery("CREATE EXTENSION IF NOT EXISTS postgis;").executeUpdate();
            entityManager.createNativeQuery("CREATE INDEX idx_restaurants_location_gist ON restaurants USING GIST(location);").executeUpdate();
            System.out.println("PostgreSQL pg_trgm extension enabled successfully");
        } catch (Exception e) {
            System.err.println(STR."Warning: Could not create pg_trgm extension: \{e.getMessage()}");
        }
    }
}