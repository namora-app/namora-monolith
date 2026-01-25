package com.namora.user.services;

import com.namora.user.dto.ApiResponse;
import com.namora.user.dto.UserRequest;
import com.namora.user.entities.Cart;
import com.namora.user.entities.Customer;
import com.namora.user.entities.Rider;
import com.namora.user.entities.User;
import com.namora.user.helpers.UserHelper;
import com.namora.user.repositories.CartRepository;
import com.namora.user.repositories.CustomerRepository;
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
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RiderRepository riderRepository;
    private final CartRepository cartRepository;

    public ResponseEntity<?> createUser(UserRequest userRequest) {
        String userId = UserContext.getCurrentUserId();
        String userRole = UserContext.getCurrentUserRole();
        if (!(userRole.equals("RIDER") || userRole.equals("CUSTOMER")))
            return new ResponseEntity<>(ApiResponse.error("Only Rider and Customers are allowed!"), HttpStatus.FORBIDDEN);
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent())
            return new ResponseEntity<>(ApiResponse.error("User already exists"), HttpStatus.CONFLICT);
        User user = new User();
        user.setId(userId);
        user.setName(userRequest.name());
        user.setPhoneNumber(userRequest.phoneNumber());
        User savedUser = userRepository.save(user);
        if (userRole.equals("CUSTOMER")) {
            Customer customer = new Customer();
            customer.setUser(savedUser);
            Customer savedCustomer = customerRepository.save(customer);
            Cart cart = new Cart();
            cart.setCustomer(savedCustomer);
            cartRepository.save(cart);
            return new ResponseEntity<>(ApiResponse.success("User Created Successfully", UserHelper.getCustomerDto(savedUser, savedCustomer)), HttpStatus.OK);
        } else {
            Rider rider = new Rider();
            rider.setUser(savedUser);
            rider.setLicenseNumber("");
            Rider savedRider = riderRepository.save(rider);
            return new ResponseEntity<>(ApiResponse.success("User Created Successfully", UserHelper.getRiderDto(savedUser, savedRider)), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> updateUser(UserRequest userRequest) {
        String userId = UserContext.getCurrentUserId();
        String userRole = UserContext.getCurrentUserRole();
        if (!(userRole.equals("RIDER") || userRole.equals("CUSTOMER")))
            return new ResponseEntity<>(ApiResponse.error("Only Rider and Customers are allowed!"), HttpStatus.FORBIDDEN);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        User userToUpdate = user.get();
        userToUpdate.setName(userRequest.name());
        userToUpdate.setPhoneNumber(userRequest.phoneNumber());
        User savedUser = userRepository.save(userToUpdate);
        if (userRole.equals("CUSTOMER")) {
            Customer customer = customerRepository.findByUser(userToUpdate);
            return new ResponseEntity<>(ApiResponse.success("User Created Successfully", UserHelper.getCustomerDto(savedUser, customer)), HttpStatus.OK);
        } else {
            Rider rider = riderRepository.findByUser(userToUpdate);
            return new ResponseEntity<>(ApiResponse.success("User Updated Successfully", UserHelper.getRiderDto(savedUser, rider)), HttpStatus.OK);
        }
    }
}
