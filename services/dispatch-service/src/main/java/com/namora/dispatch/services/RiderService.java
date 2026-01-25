package com.namora.dispatch.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RIDER_GEO_KEY = "available_riders";

    public void updateRiderLocation(String riderId, double lat, double lon) {
        redisTemplate.opsForGeo().add(RIDER_GEO_KEY, new Point(lon, lat), riderId);
    }

    public String findNearestRider(double lat, double lon) {
        Distance radius = new Distance(3, Metrics.KILOMETERS);
        Circle circle = new Circle(new Point(lon, lat), radius);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(RIDER_GEO_KEY, circle, RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().sortAscending());
        if (results != null && !results.getContent().isEmpty()) {
            return results.getContent().getFirst().getContent().getName();
        }
        return null;
    }
}
