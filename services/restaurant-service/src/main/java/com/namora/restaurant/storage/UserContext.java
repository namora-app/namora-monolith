package com.namora.restaurant.storage;

import org.springframework.stereotype.Component;

@Component
public class UserContext {
    private static final ThreadLocal<String> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserRole = new ThreadLocal<>();

    public static String getCurrentUserId() {
        return currentUserId.get();
    }

    public static String getCurrentUserRole() {
        return currentUserRole.get();
    }

    public static void setCurrentUserId(String currentUserId) {
        UserContext.currentUserId.set(currentUserId);
    }

    public static void setCurrentUserRole(String currentUserRole) {
        UserContext.currentUserRole.set(currentUserRole);
    }

    public static void clear() {
        currentUserId.remove();
        currentUserRole.remove();
    }
}
