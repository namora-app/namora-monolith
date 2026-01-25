package com.namora.user.controllers;

import com.namora.user.dto.ApiResponse;
import com.namora.user.dto.RiderRequest;
import com.namora.user.services.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riders/{riderId}")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitDetails(@RequestBody @Valid RiderRequest riderRequest, @PathVariable("riderId") String riderId) {
        try {
            return riderService.updateDetails(riderRequest, riderId);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
