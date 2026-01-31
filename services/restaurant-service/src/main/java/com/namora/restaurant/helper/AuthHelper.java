package com.namora.restaurant.helper;

import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.entities.Restaurant;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

public class AuthHelper {

    public static class Result {
        public boolean passed;
        public ResponseEntity<?> responseEntity;

        public Result(boolean passed, ResponseEntity<?> responseEntity) {
            this.passed = passed;
            this.responseEntity = responseEntity;
        }
    }

    public static Result checkRestaurantAuth(String userRole, String userId, Optional<Restaurant> optionalRestaurant) {
        System.out.println("OwnerId: " + optionalRestaurant.get().getOwnerId() + " UserId: " + userId);
        if (!userRole.equals("RESTAURANT_OWNER"))
            return new Result(false, new ResponseEntity<>("Invalid user role", HttpStatus.FORBIDDEN));
        if (optionalRestaurant.isEmpty())
            return new Result(false, new ResponseEntity<>(ApiResponse.error("Restaurant not found!"), HttpStatus.NOT_FOUND));
        if (!Objects.equals(optionalRestaurant.get().getOwnerId(), userId))
            return new Result(false, new ResponseEntity<>(ApiResponse.error("Unauthorized Access"), HttpStatus.FORBIDDEN));
        return new Result(true, null);
    }

}
