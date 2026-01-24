package com.namora.restaurant.services;

import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.dto.RestaurantScheduleRequest;
import com.namora.restaurant.dto.RestaurantScheduleResult;
import com.namora.restaurant.entities.Restaurant;
import com.namora.restaurant.entities.RestaurantSchedule;
import com.namora.restaurant.helper.AuthHelper;
import com.namora.restaurant.repositories.RestaurantRepository;
import com.namora.restaurant.repositories.RestaurantScheduleRepository;
import com.namora.restaurant.storage.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantScheduleService {

    private final RestaurantScheduleRepository restaurantScheduleRepository;
    private final RestaurantRepository restaurantRepository;

    public ResponseEntity<?> createOrUpdateRestaurantSchedule(RestaurantScheduleRequest restaurantScheduleRequest, String restaurantId) {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        AuthHelper.Result result = AuthHelper.checkRestaurantAuth(userRole, userId, optionalRestaurant);
        if(!result.passed) return result.responseEntity;
        List<RestaurantSchedule> restaurantScheduleList = restaurantScheduleRepository.findByRestaurantId(restaurantId);
        RestaurantSchedule restaurantSchedule = null;
        for (RestaurantSchedule temp : restaurantScheduleList) {
            if (temp.getDayOfWeek().toString().equals(restaurantScheduleRequest.weekDay())) {
                restaurantSchedule = temp;
                break;
            }
        }
        if (restaurantSchedule != null) {
            restaurantSchedule.setStartTime(restaurantScheduleRequest.startTime());
            restaurantSchedule.setEndTime(restaurantScheduleRequest.endTime());
            restaurantScheduleRepository.save(restaurantSchedule);
        } else {
            RestaurantSchedule newRestaurantSchedule = new RestaurantSchedule();
            newRestaurantSchedule.setStartTime(restaurantScheduleRequest.startTime());
            newRestaurantSchedule.setEndTime(restaurantScheduleRequest.endTime());
            newRestaurantSchedule.setRestaurant(optionalRestaurant.get());
            newRestaurantSchedule.setDayOfWeek(DayOfWeek.valueOf(restaurantScheduleRequest.weekDay()));
            restaurantScheduleRepository.save(newRestaurantSchedule);
        }
        List<RestaurantSchedule> restaurantSchedules = restaurantScheduleRepository.findByRestaurantId(restaurantId);
        RestaurantScheduleResult restaurantScheduleResult = new RestaurantScheduleResult(restaurantId , restaurantSchedules);
        return new ResponseEntity<>(ApiResponse.success("Updated restaurant schedules!", restaurantScheduleResult), HttpStatus.OK);
    }


    public ResponseEntity<?> findRestaurantSchedules(String restaurantId) {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        AuthHelper.Result result = AuthHelper.checkRestaurantAuth(userRole, userId, restaurantRepository.findById(restaurantId));
        if(!result.passed) return result.responseEntity;
        List<RestaurantSchedule> restaurantScheduleList = restaurantScheduleRepository.findByRestaurantId(restaurantId);
        RestaurantScheduleResult restaurantScheduleResult = new RestaurantScheduleResult(restaurantId , restaurantScheduleList);
        return new ResponseEntity<>(ApiResponse.success("Updated restaurant schedules!", restaurantScheduleResult), HttpStatus.OK);
    }
}
