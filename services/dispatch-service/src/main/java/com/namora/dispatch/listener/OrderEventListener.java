package com.namora.dispatch.listener;


import com.namora.dispatch.dto.OrderEvent;
import com.namora.dispatch.external.OrderClient;
import com.namora.dispatch.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private final RiderService riderService;
    private final OrderClient orderClient;

    @KafkaListener(topics = "order-events", groupId = "dispatch-group")
    public void handleOrderEvent(OrderEvent event) {
        if ("ORDER_CONFIRMED".equals(event.getStatus())) {
            String riderId = riderService.findNearestRider(event.getPickupLat(), event.getPickupLon());
            if (riderId != null) {
                orderClient.assignRider(event.getOrderId(), riderId);
            }
        }
    }
}
