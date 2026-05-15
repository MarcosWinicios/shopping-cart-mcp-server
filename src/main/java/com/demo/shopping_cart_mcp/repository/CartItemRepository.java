package com.demo.shopping_cart_mcp.repository;

import com.demo.shopping_cart_mcp.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByProductId(String productId);
    void deleteByProductId(String productId);
}
