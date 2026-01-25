package com.namora.user.repositories;

import com.namora.user.entities.Cart;
import com.namora.user.entities.CartItem;
import com.namora.user.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByCustomer(Customer customer);
}
