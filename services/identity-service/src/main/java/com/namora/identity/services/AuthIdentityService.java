package com.namora.identity.services;

import com.namora.identity.entities.AuthIdentity;
import com.namora.identity.repository.AuthIdentityRepository;
import com.namora.identity.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthIdentityService {

    private final AuthIdentityRepository authIdentityRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetails findAuthIdentityByEmail(String email) {
        Optional<AuthIdentity> authIdentity = authIdentityRepository.findByEmail(email);
        return authIdentity.map(CustomUserDetails::new).orElse(null);
    }
}
