package com.namora.restaurant.services;

import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.dto.RestaurantCreateRequest;
import com.namora.restaurant.entities.Restaurant;
import com.namora.restaurant.helper.AuthHelper;
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
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        AuthHelper.Result result = AuthHelper.checkRestaurantAuth(UserContext.getCurrentUserRole(), UserContext.getCurrentUserId(), optionalRestaurant);
        if(!result.passed) return result.responseEntity;
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
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        AuthHelper.Result result = AuthHelper.checkRestaurantAuth(UserContext.getCurrentUserRole(), UserContext.getCurrentUserId(), restaurant);
        if(!result.passed) return result.responseEntity;
        restaurant.get().setOpen(!restaurant.get().isOpen());
        restaurantRepository.save(restaurant.get());
        return new ResponseEntity<>(ApiResponse.success("Restaurant toggled successfully!", restaurant.get()), HttpStatus.OK);
    }
}
