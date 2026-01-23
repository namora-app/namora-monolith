package com.namora.identity.controllers;

import com.namora.identity.dto.ApiResponse;
import com.namora.identity.dto.SignUpRequest;
import com.namora.identity.services.AuthIdentityService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthIdentityService authIdentityService;

    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
        try {
            return authIdentityService.createUser(signUpRequest, response);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal Server Error Occurred!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

