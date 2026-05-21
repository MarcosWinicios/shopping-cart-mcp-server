package com.demo.shopping_cart_mcp.config;

import com.demo.shopping_cart_mcp.tools.ShoppingCartMcpService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpToolConfig {

    @Bean
    public List<ToolCallback> shoppingCartToolCallBack(ShoppingCartMcpService cartMcpService) {
        return List.of(ToolCallbacks.from(cartMcpService));

    }
}
