package com.namora.order.external;

import com.namora.order.storage.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String userId = UserContext.getCurrentUserId();
        String userRole = UserContext.getCurrentUserRole();

        if (userId != null) {
            requestTemplate.header("X-User-ID", userId);
            requestTemplate.header("X-User-Role", userRole);
        }
    }
}
