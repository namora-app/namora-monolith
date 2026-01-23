package com.namora.identity.services;

import com.namora.identity.dto.ApiResponse;
import com.namora.identity.dto.LoginRequest;
import com.namora.identity.dto.SignUpRequest;
import com.namora.identity.entities.AuthIdentity;
import com.namora.identity.entities.Role;
import com.namora.identity.helpers.AuthHelper;
import com.namora.identity.repository.AuthIdentityRepository;
import com.namora.identity.security.CustomUserDetails;
import com.namora.identity.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        if (existingUser.isPresent())
            return new ResponseEntity<>(ApiResponse.error("User already exists"), HttpStatus.CONFLICT);
        AuthIdentity authIdentity = new AuthIdentity();
        authIdentity.setEmail(signUpRequest.email());
        authIdentity.setPassword(passwordEncoder.encode(signUpRequest.password()));
        authIdentity.setRole(Role.valueOf(signUpRequest.role()));
        authIdentityRepository.save(authIdentity);
        AuthIdentity newAuthIdentity = authIdentityRepository.findByEmail(signUpRequest.email()).get();
        createAndSaveTokens(newAuthIdentity, response);
        return new ResponseEntity<>(ApiResponse.success("User created successfully!"), HttpStatus.CREATED);
    }

    public ResponseEntity<?> loginUser(LoginRequest loginRequest, HttpServletResponse response) {
        Optional<AuthIdentity> user = authIdentityRepository.findByEmail(loginRequest.email());
        if (user.isEmpty()) return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        if (!passwordEncoder.matches(loginRequest.password(), user.get().getPassword()))
            return new ResponseEntity<>(ApiResponse.error("Password is incorrect!"), HttpStatus.UNAUTHORIZED);
        createAndSaveTokens(user.get(), response);
        return new ResponseEntity<>(ApiResponse.success("User logged in successfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> refreshUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = AuthHelper.getRefreshTokenFromRequest(request);
            if (refreshToken == null)
                return new ResponseEntity<>(ApiResponse.error("Refresh token is required!"), HttpStatus.UNAUTHORIZED);
            String email = jwtUtil.getEmail(refreshToken);
            if (email == null)
                return new ResponseEntity<>(ApiResponse.error("Invalid refresh token!"), HttpStatus.UNAUTHORIZED);
            Optional<AuthIdentity> authIdentity = authIdentityRepository.findByEmail(email);
            if (authIdentity.isEmpty())
                return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
            if (!jwtUtil.isValidRefreshToken(refreshToken, new CustomUserDetails(authIdentity.get())))
                return new ResponseEntity<>(ApiResponse.error("Invalid refresh token!"), HttpStatus.UNAUTHORIZED);
            createAndSaveTokens(authIdentity.get(), response);
            return new ResponseEntity<>(ApiResponse.success("User refreshed successfully!"), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (ServletException e) {
            return new ResponseEntity<>(ApiResponse.error("Internal Server Error Occurred!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = AuthHelper.getRefreshTokenFromRequest(request);
            String accessToken = AuthHelper.getAccessTokenFromRequest(request);
            if (refreshToken != null) checkToken(refreshToken);
            else if (accessToken != null) checkToken(accessToken);
            response.addCookie(AuthHelper.createAccessTokenCookie("", 0));
            response.addCookie(AuthHelper.createRefreshTokenCookie("", 0));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return new ResponseEntity<>(ApiResponse.success("User logged out successfully!"), HttpStatus.OK);
    }

    private void checkToken(String refreshToken) {
        String email = jwtUtil.getEmail(refreshToken);
        if (email != null) {
            Optional<AuthIdentity> authIdentity = authIdentityRepository.findByEmail(email);
            if (authIdentity.isPresent()) {
                authIdentity.get().setRefreshToken("");
                authIdentityRepository.save(authIdentity.get());
            }
        }
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
