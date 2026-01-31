package com.namora.user.repositories;

import com.namora.user.entities.Cart;
import com.namora.user.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    Optional<CartItem> findByCartAndItemId(Cart cart, String itemId);

    // Spring generates the DELETE SQL automatically
    void deleteByCart(Cart cart);

    List<CartItem> findAllByCart(Cart cart);
}
