package com.namora.user.internal;

import com.namora.user.dto.ApiResponse;
import com.namora.user.services.AddressService;
import com.namora.user.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalOrderController {

    private final CartService cartService;
    private final AddressService addressService;

    @PostMapping("/carts/create-order")
    public ResponseEntity<?> createOrder() {
        try {
            return cartService.createOrder();
        } catch (Exception exception) {
            return new ResponseEntity<>(ApiResponse.error(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers/{customerId}/addresses/{addressId}")
    public ResponseEntity<?> getAddress(@PathVariable("customerId") String customerId, @PathVariable("addressId") String addressId) {
        return addressService.getAddressByCustomer(customerId, addressId);
    }

}
