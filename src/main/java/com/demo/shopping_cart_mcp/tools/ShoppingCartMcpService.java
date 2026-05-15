package com.demo.shopping_cart_mcp.tools;

import com.demo.shopping_cart_mcp.entity.CartItem;
import com.demo.shopping_cart_mcp.repository.CartItemRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShoppingCartMcpService {

    private final CartItemRepository cartItemRepository;

    public ShoppingCartMcpService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    //tools
    // catalog service
    private static final Map<String, Double> PRODUCTS = Map.of(
            "iPhone", 79999.0,
            "MacBook Air", 129999.0,
            "Boat Airdrops", 1999.0
    );

    @Tool(
            name = "addToCart",
            description = "Add a product to the shopping cart. If the product already exists, it update the quantity."
    )
    public String addToCart(@ToolParam String productName, @ToolParam int quantity) {
        if(!PRODUCTS.containsKey(productName)){
            return "Product Not Found";
        }

        Double price = PRODUCTS.get(productName);

        CartItem cartItem = cartItemRepository.findByProductId(productName);

        if(cartItem == null){
            cartItem = new CartItem();
            cartItem.setProductId(productName);
            cartItem.setProductName(productName);
            cartItem.setQuantity(quantity);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItem.setPrice(cartItem.getQuantity() * price);

        cartItemRepository.save(cartItem);

        return quantity + " " + productName + " added to cart. Total price: " + cartItem.getPrice();
    }

    @Tool(
            name = "removeFromCart",
            description = "Remove a product from the shopping cart."
    )
    public String removeCart(@ToolParam String productName) {
        if(!PRODUCTS.containsKey(productName)){
            return "Product Not Found";
        }
        cartItemRepository.deleteByProductId(productName);
        return productName + " removed from cart.";
    }

    @Tool(
            name = "getItemCart",
            description = "Fetch a product from the shopping cart."
    )
    public String getItemCart(@ToolParam String productName) {
        CartItem cartItem = cartItemRepository.findByProductId(productName);

        return "Your available item in cart is "
                + cartItem.getProductName()
                + " with quantity " + cartItem.getQuantity()
                + " and total price " + cartItem.getPrice();
    }

    @Tool(
            name = "getAllItemsCart",
            description = "Fetch all products from the shopping cart."
    )
    public List<CartItem> getAllItemsCart() {
        return cartItemRepository.findAll();
    }

    @Tool(
            name = "getCartTotal",
            description = "Calculate the total price of all items in the shopping cart."
    )
    public double getCartTotal() {
        return cartItemRepository.findAll().stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
    }

}
