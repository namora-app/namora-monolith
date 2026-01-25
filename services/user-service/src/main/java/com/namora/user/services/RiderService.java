package com.namora.user.services;

import com.namora.user.dto.ApiResponse;
import com.namora.user.dto.RiderRequest;
import com.namora.user.entities.Rider;
import com.namora.user.entities.User;
import com.namora.user.helpers.UserHelper;
import com.namora.user.repositories.RiderRepository;
import com.namora.user.repositories.UserRepository;
import com.namora.user.storage.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> updateDetails(RiderRequest riderRequest, String riderId) {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("RIDER"))
            return new ResponseEntity<>(ApiResponse.error("Only Rider are allowed!"), HttpStatus.FORBIDDEN);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        Optional<Rider> optional = riderRepository.findById(riderId);
        if (optional.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error("Rider not found!"), HttpStatus.NOT_FOUND);
        }
        Rider rider = optional.get();
        rider.setLicenseNumber(riderRequest.licenseNumber());
        rider.setVehicleNumber(riderRequest.vehicleNumber());
        rider.setApproved(true);
        Rider savedRider = riderRepository.save(rider);
        return new ResponseEntity<>(ApiResponse.success("Rider updated successfully!", UserHelper.getRiderDto(optionalUser.get(), savedRider)), HttpStatus.OK);
    }
}
