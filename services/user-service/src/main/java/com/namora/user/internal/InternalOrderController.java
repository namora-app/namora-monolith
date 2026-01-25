package com.namora.user.internal;

import com.namora.user.dto.ApiResponse;
import com.namora.user.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class InternalOrderController {

    private final CartService cartService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder() {
        try {
            return cartService.createOrder();
        } catch (Exception exception) {
            return new ResponseEntity<>(ApiResponse.error(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
