package com.namora.user.controllers;

import com.namora.user.dto.AddressRequest;
import com.namora.user.dto.ApiResponse;
import com.namora.user.services.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers/{customerId}")
@RequiredArgsConstructor
public class CustomerController {

    private final AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<?> addAddress(@PathVariable("customerId") String customerId, @RequestBody AddressRequest addressRequest) {
        try {
            return addressService.addAddress(addressRequest, customerId);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<?> updateAddress(@PathVariable("customerId") String customerId, @RequestBody AddressRequest addressRequest, @PathVariable("addressId") String addressId) {
        try {
            return addressService.updateAddress(addressRequest, customerId, addressId);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/addresses/{addressId}/default")
    public ResponseEntity<?> makeDefaultAddress(@PathVariable("customerId") String customerId, @PathVariable("addressId") String addressId) {
        try {
            return addressService.makeDefaultAddress(customerId, addressId);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
