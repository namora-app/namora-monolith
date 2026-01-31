package com.namora.order.external;

import com.namora.order.storage.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String userId = UserContext.getCurrentUserId();
        String userRole = UserContext.getCurrentUserRole();

        System.out.println("Before fiegn : UserId = " + userId+ " UserRole = " + userRole);

        if (userId != null) {
            requestTemplate.header("X-User-ID", userId);
            requestTemplate.header("X-Role", userRole);
        }
    }
}
