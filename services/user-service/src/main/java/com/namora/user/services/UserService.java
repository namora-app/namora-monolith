package com.namora.user.services;

import com.namora.user.dto.ApiResponse;
import com.namora.user.dto.UserRequest;
import com.namora.user.entities.User;
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

    public ResponseEntity<?> createUser(UserRequest userRequest) {
        String userId = UserContext.getCurrentUserId();
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent())
            return new ResponseEntity<>(ApiResponse.error("User already exists"), HttpStatus.CONFLICT);
        User user = new User();
        user.setId(userId);
        user.setName(userRequest.name());
        user.setPhoneNumber(userRequest.phoneNumber());
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(ApiResponse.success("User Created Successfully", savedUser), HttpStatus.OK);
    }

    public ResponseEntity<?> updateUser(UserRequest userRequest) {
        String userId = UserContext.getCurrentUserId();
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        User userToUpdate = user.get();
        userToUpdate.setName(userRequest.name());
        userToUpdate.setPhoneNumber(userRequest.phoneNumber());
        User savedUser = userRepository.save(userToUpdate);
        return new ResponseEntity<>(ApiResponse.success("User Updated Successfully", savedUser), HttpStatus.OK);
    }
}
