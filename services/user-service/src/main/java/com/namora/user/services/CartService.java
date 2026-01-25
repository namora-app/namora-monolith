package com.namora.user.services;

import com.namora.user.dto.ApiResponse;
import com.namora.user.dto.CartItemRequest;
import com.namora.user.dto.OrderItemRequest;
import com.namora.user.entities.Cart;
import com.namora.user.entities.CartItem;
import com.namora.user.entities.Customer;
import com.namora.user.entities.User;
import com.namora.user.repositories.CartItemRepository;
import com.namora.user.repositories.CartRepository;
import com.namora.user.repositories.CustomerRepository;
import com.namora.user.repositories.UserRepository;
import com.namora.user.storage.UserContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public ResponseEntity<?> addCartItem(CartItemRequest cartItemRequest) {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("CUSTOMER"))
            return new ResponseEntity<>(ApiResponse.error("Only Customers are allowed!"), HttpStatus.FORBIDDEN);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        Optional<Customer> optionalCustomer = Optional.ofNullable(customerRepository.findByUser(optionalUser.get()));
        if (optionalCustomer.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Customer not found!"), HttpStatus.NOT_FOUND);
        Optional<Cart> cartOptional = cartRepository.findByCustomer(optionalCustomer.get());
        if (cartOptional.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Cart not found!"), HttpStatus.NOT_FOUND);
        Optional<CartItem> optionalCartItem = cartItemRepository.findByItemId(cartItemRequest.itemId(), cartOptional.get());
        if (optionalCartItem.isEmpty()) {
            CartItem cartItem = new CartItem();
            cartItem.setItemId(cartItemRequest.itemId());
            cartItem.setQuantity(cartItemRequest.quantity());
            cartItem.setCart(cartOptional.get());
            cartItemRepository.save(cartItem);
            Optional<CartItem> response = cartItemRepository.findByItemId(cartItemRequest.itemId(), cartOptional.get());
            return new ResponseEntity<>(ApiResponse.success("CartItem added successfully!", response.get()), HttpStatus.CREATED);
        } else {
            optionalCartItem.get().setQuantity(cartItemRequest.quantity());
            cartItemRepository.save(optionalCartItem.get());
            Optional<CartItem> response = cartItemRepository.findByItemId(cartItemRequest.itemId(), cartOptional.get());
            return new ResponseEntity<>(ApiResponse.success("CartItem updated successfully!", response.get()), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> clearCart() {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("CUSTOMER"))
            return new ResponseEntity<>(ApiResponse.error("Only Customers are allowed!"), HttpStatus.FORBIDDEN);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        Optional<Customer> optionalCustomer = Optional.ofNullable(customerRepository.findByUser(optionalUser.get()));
        if (optionalCustomer.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Customer not found!"), HttpStatus.NOT_FOUND);
        Optional<Cart> cartOptional = cartRepository.findByCustomer(optionalCustomer.get());
        if (cartOptional.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Cart not found!"), HttpStatus.NOT_FOUND);
        cartItemRepository.deleteByCart(cartOptional.get());
        return new ResponseEntity<>(ApiResponse.success("Cleared the cart!"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> createOrder() {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("CUSTOMER"))
            return new ResponseEntity<>(ApiResponse.error("Only Customers are allowed!"), HttpStatus.FORBIDDEN);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        Optional<Customer> optionalCustomer = Optional.ofNullable(customerRepository.findByUser(optionalUser.get()));
        if (optionalCustomer.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Customer not found!"), HttpStatus.NOT_FOUND);
        Optional<Cart> cartOptional = cartRepository.findByCustomer(optionalCustomer.get());
        if (cartOptional.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Cart not found!"), HttpStatus.NOT_FOUND);
        List<CartItem> cartItems = cartItemRepository.findAllByCart(cartOptional.get());
        List<OrderItemRequest> orderItemRequests = cartItems.stream().map(cartItem -> new OrderItemRequest(cartItem.getItemId(), cartItem.getQuantity())).toList();
        cartItemRepository.deleteByCart(cartOptional.get());
        return new ResponseEntity<>(ApiResponse.success("Order item fetched successfully!", Map.of("customerId", optionalCustomer.get().getCustomerId(), "orderItems", orderItemRequests)), HttpStatus.OK);
    }
}
