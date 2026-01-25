package com.namora.user.helpers;

import com.namora.user.dto.CustomerDto;
import com.namora.user.dto.RiderDto;
import com.namora.user.entities.Customer;
import com.namora.user.entities.Rider;
import com.namora.user.entities.User;

public class UserHelper {
    public static CustomerDto getCustomerDto(User user, Customer customer) {
        return new CustomerDto(
                user.getId(),
                user.getName(),
                user.getPhoneNumber(),
                user.getProfileImageUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                customer.getCustomerId()
        );
    }

    public static RiderDto getRiderDto(User user, Rider rider) {
        return new RiderDto(
                user.getId(),
                user.getName(),
                user.getPhoneNumber(),
                user.getProfileImageUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                rider.getRiderId(),
                rider.getLicenseNumber(),
                rider.getVehicleNumber(),
                rider.isApproved(),
                rider.getRating()
        );
    }
}
