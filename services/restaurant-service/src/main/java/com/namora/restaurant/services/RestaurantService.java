package com.namora.restaurant.services;

import com.namora.restaurant.dto.Address;
import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.dto.RestaurantCreateRequest;
import com.namora.restaurant.entities.Restaurant;
import com.namora.restaurant.helper.AuthHelper;
import com.namora.restaurant.repositories.RestaurantRepository;
import com.namora.restaurant.storage.UserContext;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private static final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    private final RestaurantRepository restaurantRepository;

    public ResponseEntity<?> createRestaurant(RestaurantCreateRequest restaurantCreateRequest) {
        String userId = UserContext.getCurrentUserId();
        String userRole = UserContext.getCurrentUserRole();
        if (!userRole.equals("RESTAURANT_OWNER")) {
            return new ResponseEntity<>("Invalid user role", HttpStatus.FORBIDDEN);
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId(userId);
        Restaurant savedRestaurant = fillRestaurantDetails(restaurantCreateRequest, restaurant);
        return new ResponseEntity<>(ApiResponse.success("Restaurant registered successfully!", savedRestaurant), HttpStatus.OK);
    }

    public ResponseEntity<?> updateRestaurant(String restaurantId, RestaurantCreateRequest restaurantCreateRequest) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        AuthHelper.Result result = AuthHelper.checkRestaurantAuth(UserContext.getCurrentUserRole(), UserContext.getCurrentUserId(), optionalRestaurant);
        if (!result.passed) return result.responseEntity;
        Restaurant restaurant = optionalRestaurant.get();
        Restaurant savedRestaurant = fillRestaurantDetails(restaurantCreateRequest, restaurant);
        return new ResponseEntity<>(ApiResponse.success("Restaurant updated successfully!", savedRestaurant), HttpStatus.OK);
    }

    private Restaurant fillRestaurantDetails(RestaurantCreateRequest restaurantCreateRequest, Restaurant restaurant) {
        restaurant.setName(restaurantCreateRequest.name());
        restaurant.setAddress(restaurantCreateRequest.address());
        restaurant.setLatitude(restaurantCreateRequest.latitude());
        restaurant.setLongitude(restaurantCreateRequest.longitude());
        restaurant.setFssaiLicense(restaurantCreateRequest.fssaiLicense());
        Point location = geometryFactory.createPoint(
                new Coordinate(restaurantCreateRequest.longitude(), restaurantCreateRequest.latitude())
        );
        restaurant.setLocation(location);
        return restaurantRepository.save(restaurant);
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
        if (!result.passed) return result.responseEntity;
        restaurant.get().setOpen(!restaurant.get().isOpen());
        restaurantRepository.save(restaurant.get());
        return new ResponseEntity<>(ApiResponse.success("Restaurant toggled successfully!", restaurant.get()), HttpStatus.OK);
    }

    public ResponseEntity<?> getLocation(String restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isEmpty()) return new ResponseEntity<>("Restaurant not found!", HttpStatus.NOT_FOUND);
        Address address = new Address();
        address.setAddress(restaurant.get().getAddress());
        address.setLatitude(restaurant.get().getLatitude());
        address.setLongitude(restaurant.get().getLongitude());
        return  new ResponseEntity<>(ApiResponse.success("Restaurant found!", restaurant.get()), HttpStatus.OK);
    }
}
