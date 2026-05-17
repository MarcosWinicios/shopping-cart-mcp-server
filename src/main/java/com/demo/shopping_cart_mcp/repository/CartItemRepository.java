package com.demo.shopping_cart_mcp.repository;

import com.demo.shopping_cart_mcp.entity.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByProductId(String productId);
    @Transactional
    void deleteByProductId(String productId);
}
