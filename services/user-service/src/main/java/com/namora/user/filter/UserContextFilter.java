package com.namora.user.filter;

import com.namora.user.storage.UserContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String userId = request.getHeader("X-User-ID");
        String userRole = request.getHeader("X-Role");
        System.out.println("UserContextFilter: userId = " + userId + ", userRole = " + userRole);
        if (userId != null) {
            UserContext.setCurrentUserId(userId);
            UserContext.setCurrentUserRole(userRole);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.clear();
        }
    }
}

