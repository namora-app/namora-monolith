package com.namora.restaurant.services;

import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.dto.ItemRequest;
import com.namora.restaurant.entities.Item;
import com.namora.restaurant.entities.Restaurant;
import com.namora.restaurant.helper.AuthHelper;
import com.namora.restaurant.repositories.ItemRepository;
import com.namora.restaurant.repositories.RestaurantRepository;
import com.namora.restaurant.storage.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final RestaurantRepository restaurantRepository;

    public ResponseEntity<?> addItem(String restaurantId, ItemRequest itemRequest) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        AuthHelper.Result result = AuthHelper.checkRestaurantAuth(UserContext.getCurrentUserRole(), UserContext.getCurrentUserId(), restaurantOptional);
        if (!result.passed) return result.responseEntity;
        Item item = new Item();
        item.setRestaurant(restaurantOptional.get());
        item.setName(itemRequest.name());
        item.setDescription(itemRequest.description());
        item.setPrice(itemRequest.price());
        item.setVeg(itemRequest.isVeg());
        itemRepository.save(item);
        return new ResponseEntity<>(ApiResponse.success("Item added successfully!", itemRepository.findByRestaurantId(restaurantId)), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateItem(String restaurantId, String itemId, ItemRequest itemRequest) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        AuthHelper.Result result = AuthHelper.checkRestaurantAuth(UserContext.getCurrentUserRole(), UserContext.getCurrentUserId(), restaurantOptional);
        if (!result.passed) return result.responseEntity;
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Item not found!"), HttpStatus.NOT_FOUND);
        Item item = itemOptional.get();
        item.setName(itemRequest.name());
        item.setDescription(itemRequest.description());
        item.setPrice(itemRequest.price());
        item.setVeg(itemRequest.isVeg());
        itemRepository.save(item);
        return new ResponseEntity<>(ApiResponse.success("Item updated successfully!", itemRepository.findById(itemId)), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteItem(String restaurantId, String itemId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        AuthHelper.Result result = AuthHelper.checkRestaurantAuth(UserContext.getCurrentUserRole(), UserContext.getCurrentUserId(), restaurant);
        if (!result.passed) return result.responseEntity;
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Item not found!"), HttpStatus.NOT_FOUND);
        itemRepository.delete(itemOptional.get());
        return new ResponseEntity<>(ApiResponse.success("Item removed successfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> getAllItemsByRestaurant(String restaurantId) {
        List<Item> items = itemRepository.findByRestaurantId(restaurantId);
        return new ResponseEntity<>(ApiResponse.success("Items found successfully!", items), HttpStatus.OK);
    }

    public ResponseEntity<?> getTopItems(String name, double latitude, double longitude) {
        // all items having similar item name passed in query , I want 10 items (if exact name matching not found , find on the basis of similarity score)
        // topPriority less distance
        // if distance matches then higher rating comes first
        return new ResponseEntity<>(ApiResponse.success("Items fetched successfully!", itemRepository.getTopItems(name, latitude, longitude)), HttpStatus.OK);
    }

}
