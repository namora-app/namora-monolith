package com.namora.restaurant.services;

import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.dto.RestaurantCreateRequest;
import com.namora.restaurant.entities.Restaurant;
import com.namora.restaurant.repositories.RestaurantRepository;
import com.namora.restaurant.storage.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public ResponseEntity<?> createRestaurant(RestaurantCreateRequest restaurantCreateRequest) {
        String userId = UserContext.getCurrentUserId();
        String userRole = UserContext.getCurrentUserRole();
        if (!userRole.equals("RESTAURANT_OWNER")) {
            return new ResponseEntity<>("Invalid user role", HttpStatus.FORBIDDEN);
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId(userId);
        restaurant.setName(restaurantCreateRequest.name());
        restaurant.setAddress(restaurantCreateRequest.address());
        restaurant.setLatitude(restaurantCreateRequest.latitude());
        restaurant.setLongitude(restaurantCreateRequest.longitude());
        restaurant.setFssaiLicense(restaurantCreateRequest.fssaiLicense());
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return new ResponseEntity<>(ApiResponse.success("Restaurant registered successfully!", savedRestaurant), HttpStatus.OK);
    }

    public ResponseEntity<?> updateRestaurant(String restaurantId, RestaurantCreateRequest restaurantCreateRequest) {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("RESTAURANT_OWNER")) {
            return new ResponseEntity<>("Invalid user role", HttpStatus.FORBIDDEN);
        }
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        if (optionalRestaurant.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Restaurant not found!"), HttpStatus.NOT_FOUND);
        if (!Objects.equals(optionalRestaurant.get().getOwnerId(), userId))
            return new ResponseEntity<>(ApiResponse.error("Unauthorized Access"), HttpStatus.FORBIDDEN);
        Restaurant restaurant = optionalRestaurant.get();
        restaurant.setName(restaurantCreateRequest.name());
        restaurant.setAddress(restaurantCreateRequest.address());
        restaurant.setLatitude(restaurantCreateRequest.latitude());
        restaurant.setLongitude(restaurantCreateRequest.longitude());
        restaurant.setFssaiLicense(restaurantCreateRequest.fssaiLicense());
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return new ResponseEntity<>(ApiResponse.success("Restaurant updated successfully!", savedRestaurant), HttpStatus.OK);
    }

    public ResponseEntity<?> getAllRestaurants() {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("RESTAURANT_OWNER")) {
            return new ResponseEntity<>("Invalid user role", HttpStatus.FORBIDDEN);
        }
        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(userId);
        return new ResponseEntity<>(ApiResponse.success("Restaurants found!", restaurants), HttpStatus.OK);
    }

    public ResponseEntity<?> toggleOpenStatus(String restaurantId) {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("RESTAURANT_OWNER")) {
            return new ResponseEntity<>("Invalid user role", HttpStatus.FORBIDDEN);
        }
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error("Restaurant not found!"), HttpStatus.NOT_FOUND);
        }
        if (!restaurant.get().getOwnerId().equals(userId))
            return new ResponseEntity<>(ApiResponse.error("Unauthorized Access"), HttpStatus.FORBIDDEN);
        restaurant.get().setOpen(!restaurant.get().isOpen());
        restaurantRepository.save(restaurant.get());
        return new ResponseEntity<>(ApiResponse.success("Restaurant toggled successfully!", restaurant.get()), HttpStatus.OK);
    }
}
