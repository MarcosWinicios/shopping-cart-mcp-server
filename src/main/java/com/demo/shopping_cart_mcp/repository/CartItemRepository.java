package com.demo.shopping_cart_mcp.repository;

import com.demo.shopping_cart_mcp.entity.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartItemRepository extends MongoRepository<CartItem,String> {

    CartItem findByProductId(String productId);
    void  deleteByProductId(String productId);
}
