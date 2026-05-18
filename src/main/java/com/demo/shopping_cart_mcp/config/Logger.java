package com.demo.shopping_cart_mcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Component
public class Logger {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludePayload(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(false);
        filter.setMaxPayloadLength(10000);
        return filter;
    }
}
