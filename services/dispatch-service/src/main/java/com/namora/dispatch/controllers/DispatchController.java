package com.namora.dispatch.controllers;

import com.namora.dispatch.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dispatch/riders")
@RequiredArgsConstructor
public class DispatchController {

    private final RiderService riderService;

    @PostMapping("/{riderId}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable("riderId") String riderId, @RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        riderService.updateRiderLocation(riderId, lat, lon);
        return ResponseEntity.ok().build();
    }
}
