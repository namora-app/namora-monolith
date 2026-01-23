package com.namora.identity.controllers;

import com.namora.identity.dto.ApiResponse;
import com.namora.identity.dto.LoginRequest;
import com.namora.identity.dto.SignUpRequest;
import com.namora.identity.services.AuthIdentityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthIdentityService authIdentityService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
        try {
            return authIdentityService.createUser(signUpRequest, response);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal Server Error Occurred!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            return authIdentityService.loginUser(loginRequest, response);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal Server Error Occurred!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            return authIdentityService.refreshUser(request, response);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal Server Error Occurred!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            return authIdentityService.logoutUser(request, response);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal Server Error Occurred!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

