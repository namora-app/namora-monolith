package com.namora.identity.helpers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Arrays;

public class AuthHelper {

    public static String getAccessTokenFromRequest(HttpServletRequest request) throws ServletException, IOException {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies()).filter(
                    cookie ->
                            "accessToken".equals(cookie.getName())
            ).map(Cookie::getValue).findFirst().orElse(null);
        }
        return null;
    }

    public static String getRefreshTokenFromRequest(HttpServletRequest request) throws ServletException, IOException {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies()).filter(
                    cookie ->
                            "refreshToken".equals(cookie.getName())
            ).map(Cookie::getValue).findFirst().orElse(null);
        }
        return null;
    }

    public static Cookie createAccessTokenCookie(String accessToken, int maxAge) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    public static Cookie createRefreshTokenCookie(String refreshToken, int maxAge) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
