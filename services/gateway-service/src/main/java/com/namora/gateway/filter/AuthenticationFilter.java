package com.namora.gateway.filter;

import com.namora.gateway.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
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
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
                }
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    if (!jwtUtil.isValidAccessToken(authHeader)) {
                        return onError(exchange, "Invalid Token!", HttpStatus.UNAUTHORIZED);
                    }

                    String userId = jwtUtil.getUserId(authHeader);
                    String userRole = jwtUtil.getRole(authHeader);

                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", userId)
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
