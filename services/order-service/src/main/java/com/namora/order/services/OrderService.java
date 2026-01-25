package com.namora.order.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namora.order.dto.*;
import com.namora.order.entities.Order;
import com.namora.order.entities.OrderItem;
import com.namora.order.entities.OrderStatus;
import com.namora.order.external.RestaurantClient;
import com.namora.order.external.UserClient;
import com.namora.order.repositories.OrderRepository;
import com.namora.order.storage.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final UserClient userClient;
    private final RestaurantClient restaurantClient;

    private final BigDecimal platformFee = new BigDecimal("5.00");
    private final BigDecimal deliveryFee = new BigDecimal("30.00");
    private final BigDecimal taxPercentCharge = new BigDecimal("0.05");

    @Transactional
    public ResponseEntity<?> placeOrder(String addressId) {
        String userId = UserContext.getCurrentUserId();
        String userRole = UserContext.getCurrentUserRole();

        if (userId == null || userRole == null || !userRole.equals("CUSTOMER")) {
            return new ResponseEntity<>(ApiResponse.error("Only customers are allowed!"), HttpStatus.FORBIDDEN);
        }

        ApiResponse<?> cartItemsResponse = userClient.createOrder();
        if (!cartItemsResponse.success()) {
            return new ResponseEntity<>(cartItemsResponse, HttpStatus.BAD_REQUEST);
        }

        OrderItemRequest orderItemRequest = objectMapper.convertValue(cartItemsResponse.data(), OrderItemRequest.class);
        List<CartItemRequest> cartItems = orderItemRequest.getOrderItems();

        if (cartItems == null || cartItems.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error("Cart is empty"), HttpStatus.BAD_REQUEST);
        }

        try {
            Map<String, List<ItemDetails>> itemsByRestaurant = mapByRestaurant(cartItems);
            List<Order> savedOrders = createAndSaveOrders(itemsByRestaurant, cartItems, userId, addressId);
            return ResponseEntity.ok(ApiResponse.success("Order Success!", savedOrders));
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(STR."Order Failed: \{e.getMessage()}"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, List<ItemDetails>> mapByRestaurant(List<CartItemRequest> cartItems) {
        List<String> itemIds = cartItems.stream().map(CartItemRequest::itemId).toList();
        List<? extends ApiResponse<?>> itemDetailsResponse = itemIds.stream()
                .map(restaurantClient::getItemDetails)
                .toList();

        Map<String, List<ItemDetails>> restaurantIdMap = new HashMap<>();

        for (ApiResponse<?> response : itemDetailsResponse) {
            if (response.success()) {
                ItemDetails itemDetail = objectMapper.convertValue(response.data(), ItemDetails.class);
                restaurantIdMap.computeIfAbsent(itemDetail.restaurantId(), _ -> new ArrayList<>()).add(itemDetail);
            }
        }
        return restaurantIdMap;
    }

    private List<Order> createAndSaveOrders(Map<String, List<ItemDetails>> restaurantIdMap,
                                            List<CartItemRequest> cartItems,
                                            String customerId,
                                            String addressId) throws JsonProcessingException {

        List<Order> orders = new ArrayList<>();
        Address customerAddress = getCustomerAddress(customerId, addressId);
        String customerAddressJson = objectMapper.writeValueAsString(customerAddress);

        Map<String, Integer> quantityMap = cartItems.stream()
                .collect(Collectors.toMap(CartItemRequest::itemId, CartItemRequest::quantity));

        for (Map.Entry<String, List<ItemDetails>> entry : restaurantIdMap.entrySet()) {
            String restaurantId = entry.getKey();
            List<ItemDetails> itemDetailsList = entry.getValue();

            Address restaurantAddress = getRestaurantAddress(restaurantId);

            Order order = new Order();
            order.setCustomerId(customerId);
            order.setRestaurantId(restaurantId);
            order.setStatus(OrderStatus.CREATED);
            order.setDeliveryAddressJson(customerAddressJson);
            order.setDropLat(customerAddress.getLatitude());
            order.setDropLng(customerAddress.getLongitude());
            order.setPickupLat(restaurantAddress.getLatitude());
            order.setPickupLng(restaurantAddress.getLongitude());

            BigDecimal totalItemAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();

            for (ItemDetails item : itemDetailsList) {
                int quantity = quantityMap.getOrDefault(item.itemId(), 1);

                BigDecimal discountFactor = BigDecimal.ONE.subtract(item.discountPercent() != null ? item.discountPercent() : BigDecimal.ZERO);
                BigDecimal finalItemPrice = item.price().multiply(discountFactor);
                BigDecimal lineTotal = finalItemPrice.multiply(BigDecimal.valueOf(quantity));

                totalItemAmount = totalItemAmount.add(lineTotal);

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .itemId(item.itemId())
                        .itemName(item.name())
                        .quantity(quantity)
                        .priceAtTimeOfOrder(finalItemPrice)
                        .build();

                orderItems.add(orderItem);
            }

            BigDecimal tax = totalItemAmount.multiply(taxPercentCharge).setScale(2, RoundingMode.HALF_UP);
            BigDecimal finalAmount = totalItemAmount.add(tax).add(deliveryFee).add(platformFee);

            order.setTotalItemAmount(totalItemAmount);
            order.setTax(tax);
            order.setDeliveryFee(deliveryFee);
            order.setPlatformFee(platformFee);
            order.setFinalAmount(finalAmount);
            order.setItems(orderItems);

            Order savedOrder = orderRepository.save(order);
            orders.add(savedOrder);

            kafkaTemplate.send("order-events", savedOrder.getId(), new OrderEvent(
                    savedOrder.getId(),
                    savedOrder.getRestaurantId(),
                    String.valueOf(savedOrder.getStatus()),
                    savedOrder.getPickupLat(),
                    savedOrder.getPickupLng(),
                    savedOrder.getDropLat(),
                    savedOrder.getDropLng()
            ));
        }

        return orders;
    }

    private Address getCustomerAddress(String customerId, String addressId) {
        ApiResponse<?> addressResponse = userClient.getAddress(customerId, addressId);
        if (!addressResponse.success()) {
            throw new RuntimeException("Customer Address not found or invalid!");
        }
        return objectMapper.convertValue(addressResponse.data(), Address.class);
    }

    private Address getRestaurantAddress(String restaurantId) {
        ApiResponse<?> addressResponse = restaurantClient.getRestaurantAddress(restaurantId);
        if (!addressResponse.success()) {
            throw new RuntimeException(STR."Restaurant Address not found for ID: \{restaurantId}");
        }
        return objectMapper.convertValue(addressResponse.data(), Address.class);
    }
}