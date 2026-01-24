package com.namora.gateway.filter;

import com.namora.gateway.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String pathUrl = exchange.getRequest().getURI().getPath();
            if (!pathUrl.startsWith("/auth")) {
                String token = null;

                MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
                if (cookies.containsKey("accessToken")) {
                    HttpCookie cookie = cookies.getFirst("accessToken");
                    if (cookie != null) {
                        token = cookie.getValue();
                    }
                }

                if (token == null && exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                    }
                }

                if (token == null) {
                    return onError(exchange, "Missing Authorization Token", HttpStatus.UNAUTHORIZED);
                }

                try {
                    if (!jwtUtil.isValidAccessToken(token)) {
                        return onError(exchange, "Invalid Token!", HttpStatus.UNAUTHORIZED);
                    }
                    String userId = jwtUtil.getUserId(token);
                    String userRole = jwtUtil.getRole(token);
                    System.out.println("User ID: " + userId + " Role: " + userRole);

                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-User-ID", userId)
                            .header("X-Role", userRole)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                } catch (Exception e) {
                    return onError(exchange, "Invalid Token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}