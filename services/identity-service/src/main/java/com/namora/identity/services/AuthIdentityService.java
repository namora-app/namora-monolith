package com.namora.identity.services;

import com.namora.identity.dto.ApiResponse;
import com.namora.identity.dto.SignUpRequest;
import com.namora.identity.entities.AuthIdentity;
import com.namora.identity.entities.Role;
import com.namora.identity.helpers.AuthHelper;
import com.namora.identity.repository.AuthIdentityRepository;
import com.namora.identity.security.CustomUserDetails;
import com.namora.identity.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthIdentityService {

    private final AuthIdentityRepository authIdentityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public CustomUserDetails findAuthIdentityByEmail(String email) {
        Optional<AuthIdentity> authIdentity = authIdentityRepository.findByEmail(email);
        return authIdentity.map(CustomUserDetails::new).orElse(null);
    }

    public ResponseEntity<?> createUser(SignUpRequest signUpRequest, HttpServletResponse response) {
        Optional<AuthIdentity> existingUser = authIdentityRepository.findByEmail(signUpRequest.email());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>(ApiResponse.error("User already exists"), HttpStatus.CONFLICT);
        }
        AuthIdentity authIdentity = new AuthIdentity();
        authIdentity.setEmail(signUpRequest.email());
        authIdentity.setPassword(passwordEncoder.encode(signUpRequest.password()));
        authIdentity.setRole(Role.valueOf(signUpRequest.role()));
        authIdentityRepository.save(authIdentity);
        AuthIdentity newAuthIdentity = authIdentityRepository.findByEmail(signUpRequest.email()).get();
        createAndSaveTokens(newAuthIdentity, response);
        return new ResponseEntity<>(ApiResponse.success("User created successfully!"), HttpStatus.CREATED);
    }

    private void createAndSaveTokens(AuthIdentity authIdentity, HttpServletResponse response) {
        CustomUserDetails userDetails = new CustomUserDetails(authIdentity);
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        response.addCookie(AuthHelper.createAccessTokenCookie(accessToken, 15 * 60 * 60));
        response.addCookie(AuthHelper.createRefreshTokenCookie(refreshToken, 7 * 24 * 60 * 60));
        authIdentity.setRefreshToken(refreshToken);
        authIdentityRepository.save(authIdentity);
    }
}
