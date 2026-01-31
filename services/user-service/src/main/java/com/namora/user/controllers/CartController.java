package com.namora.user.controllers;

import com.namora.user.dto.ApiResponse;
import com.namora.user.dto.CartItemRequest;
import com.namora.user.services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    @PostMapping("/add")
    public ResponseEntity<?> addCartItem(@Valid @RequestBody CartItemRequest cartItemRequest) {
        try {
            return cartService.addCartItem(cartItemRequest);
        } catch (Exception ex) {
            return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearCart() {
        try {
            return cartService.clearCart();
        } catch (Exception ex) {
            return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
