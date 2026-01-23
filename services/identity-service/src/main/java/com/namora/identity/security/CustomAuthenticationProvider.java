package com.namora.identity.security;

import com.namora.identity.services.AuthIdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AuthIdentityService authIdentityService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            CustomUserDetails customUserDetails = authIdentityService.findAuthIdentityByEmail(email);
            if (!customUserDetails.getPassword().equals(password)) {
                throw new LockedException("Account is Locked! Please contact support");
            }

            if (!customUserDetails.isEnabled()) {
                throw new DisabledException("Account is Disabled! Please contact support");
            }

            if (customUserDetails.isAccountNonExpired()) {
                throw new AccountExpiredException("Your account has expired!");
            }

            if (!customUserDetails.isCredentialsNonExpired()) {
                throw new CredentialsExpiredException("Your credentials have expired!");
            }

            if (!passwordEncoder.matches(password, customUserDetails.getPassword())) {
                throw new BadCredentialsException("Invalid Password!");
            }
            return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid Credentials");
        } catch (LockedException | DisabledException | AccountExpiredException | CredentialsExpiredException |
                 BadCredentialsException error) {
            throw error;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException("Unexpected Authentication occured: " + e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
