package com.namora.restaurant.filter;

import com.namora.restaurant.storage.UserContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String userId = request.getParameter("X-User-ID");
        String userRole = request.getParameter("X-Role");
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
