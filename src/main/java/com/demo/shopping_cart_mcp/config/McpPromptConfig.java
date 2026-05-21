package com.demo.shopping_cart_mcp.config;


import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpPromptConfig {

    @Bean
    public List<McpServerFeatures.SyncPromptSpecification> prompts() {

        /*
         * Argumento esperado pelo prompt
         */
        McpSchema.PromptArgument categoryArgument =
                new McpSchema.PromptArgument(
                        "category",
                        "Categoria dos produtos desejados",
                        true
                );

        /*
         * Definição do prompt
         */
        McpSchema.Prompt prompt =
                new McpSchema.Prompt(
                        "recommend-products",
                        "Sugere produtos de uma categoria",
                        List.of(categoryArgument)
                );

        /*
         * Implementação do prompt
         */
        McpServerFeatures.SyncPromptSpecification specification =
                new McpServerFeatures.SyncPromptSpecification(

                        prompt,

                        (exchange, request) -> {

                            String category =
                                    request.arguments().get("category").toString();

                            String text = """
                                    Você é um assistente de e-commerce.

                                    Sugira 3 produtos da categoria: %s

                                    Para cada produto:
                                    - explique brevemente
                                    - diga para quem é indicado
                                    - destaque um diferencial
                                    """.formatted(category);

                            McpSchema.PromptMessage message =
                                    new McpSchema.PromptMessage(
                                            McpSchema.Role.USER,
                                            new McpSchema.TextContent(text)
                                    );

                            return new McpSchema.GetPromptResult(
                                    "Prompt de recomendação gerado",
                                    List.of(message)
                            );
                        }
                );

        return List.of(specification);
    }
}
