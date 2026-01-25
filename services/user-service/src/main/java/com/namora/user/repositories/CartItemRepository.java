package com.namora.user.repositories;

import com.namora.user.entities.Cart;
import com.namora.user.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    @Query("SELECT r FROM CartItem r WHERE r.cart = :cart and r.itemId = :itemId")
    Optional<CartItem> findByItemId(String itemId, Cart cart);

    @Query("DELETE FROM CartItem r WHERE r.cart = :cart")
    void deleteByCart(Cart cart);

    Cart cart(Cart cart);

    List<CartItem> findAllByCart(Cart cart);
}
