package com.demo.shopping_cart_mcp.config;


import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpResourceConfig {

    @Bean
    public List<McpServerFeatures.SyncResourceSpecification> resources() {

        McpSchema.Resource resource = new McpSchema.Resource(
                "policy://shipping",
                "Política de Frete",
                "Regras de frete da loja",
                "text/plain",
                null
        );

        McpServerFeatures.SyncResourceSpecification specification =
                new McpServerFeatures.SyncResourceSpecification(

                        resource,

                        (exchange, request) -> {

                            String content = """
                                    Frete grátis para compras acima de R$ 200.
                                    Prazo médio de entrega: 5 dias úteis.
                                    """;

                            McpSchema.TextResourceContents resourceContents =
                                    new McpSchema.TextResourceContents(
                                            request.uri(),
                                            "text/plain",
                                            content
                                    );

                            return new McpSchema.ReadResourceResult(
                                    List.of(resourceContents)
                            );
                        }
                );

        return List.of(specification);
    }
}