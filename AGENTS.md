# AGENTS.md - Developer Guide

Guia técnico para desenvolvedores trabalhando no Shopping Cart MCP Server. Este documento fornece contexto arquitetural, padrões de desenvolvimento e instruções para tarefas comuns.

## Project Overview

Este é um **servidor MCP Spring Boot** que expõe operações de carrinho de compras como ferramentas acessíveis por IA via Model Context Protocol. O servidor utiliza PostgreSQL para armazenamento persistente e o framework MCP do Spring AI para definir ferramentas que clientes IA externos (como Claude, Copilot) podem invocar.

**Arquitetura chave**: Client → MCP Server (stdio) → Spring Application → PostgreSQL

## Critical System Understanding

### MCP Server Pattern (Not a Web Server)

Este **NÃO** é uma REST API. É um servidor stdio-based que:
- Executa como um processo standalone
- Comunica via stdin/stdout com clientes MCP
- **Configuração crítica**: `application.yaml` possui `web-application-type: none` e `stdio: true` — estas **NÃO** devem mudar
- Ferramentas são expostas via anotações `@Tool` em métodos de serviço
- A aplicação `ShoppingCartMcpApplication` registra ferramentas via `ToolCallbacks.from(cartMcpService)` em uma `List<ToolCallback>`

### Tool Registration Flow

1. Classe `@Service` (`ShoppingCartMcpService`) contém métodos anotados com `@Tool`
2. `ShoppingCartMcpApplication` cria um bean `List<ToolCallback>` envolvendo métodos de serviço
3. A auto-configuração MCP do Spring AI descobre e registra estas como ferramentas
4. **Ao adicionar ferramentas**: Garanta que a assinatura do método tenha anotações `@ToolParam` para cada parâmetro

Padrão exemplo em `ShoppingCartMcpService`:
```java
@Tool(name = "addToCart", description = "...")
public String addToCart(@ToolParam String productName, @ToolParam int quantity)
```

### Data Flow

1. **Catálogo**: Mapa estático `Map<String, Double> PRODUCTS` em `ShoppingCartMcpService` (adicionar novos produtos aqui)
2. **Entidade**: Entidade `CartItem` com anotação JPA `@Entity`
3. **Persistência**: `CartItemRepository` estende `JpaRepository` com queries customizadas:
   - `findByProductId(String productId)` - usada para lógica de upsert
   - `deleteByProductId(String productId)` - usada para remoção
4. **Métodos de Ferramenta**: Usam repositório para persistir/consultar estado do carrinho

**Nota Crítica**: `CartItem.productId` é usado para lookups; garanta que nomes únicos de produtos correspondam exatamente às chaves do mapa `PRODUCTS`.

## Build & Runtime

### Maven Build
- **Java 21** obrigatório (configurado em `pom.xml`)
- Build: `./mvnw clean package` → `target/shopping-cart-server.jar`
- Testes: `./mvnw test` (atualmente apenas teste básico de carregamento de contexto)
- **Lombok habilitado**: Annotation processor configurado no plugin do compilador

### Executar o Servidor
```bash
./mvnw spring-boot:run
# OU
java -jar target/shopping-cart-server.jar
```

Servidor inicia via stdout/stdin aguardando conexões de cliente MCP. Nenhuma porta HTTP necessária.

### Configuração PostgreSQL

Configurado em `application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shopping-cart-mcp
    username: postgres
    password: 123456
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
```

**Para rodar PostgreSQL**:

Certifique-se de ter PostgreSQL instalado localmente e crie o banco de dados:
```bash
createdb -U postgres -h localhost shopping-cart-mcp
```

Hibernate cria/atualiza automaticamente a tabela `cart_items` no startup (`ddl-auto: update`). A configuração `ddl-auto: update` cria/atualiza automaticamente tabelas a partir de definições de `@Entity`.

## Development Patterns & Conventions

### Adding New Tools
1. Add method to `ShoppingCartMcpService` with `@Tool` and `@ToolParam` annotations
2. Keep descriptions concise (used by AI clients to decide when to call)
3. Return `String` for single results or `List<CartItem>` for collections
4. Tools can access `cartItemRepository` via constructor injection
5. **Return strings** for user-facing feedback; return objects for structured data

### Adding New Products
Edit the `PRODUCTS` map in `ShoppingCartMcpService`:
```java
private static final Map<String, Double> PRODUCTS = Map.of(
    "iPhone", 79999.0,
    "MacBook Air", 129999.0,
    "Boat Airdrops", 1999.0
);
```

**Must match** product names used in tool calls (case-sensitive).

### Entity Design (CartItem)
- Uses JPA `@GeneratedValue(strategy = GenerationType.IDENTITY)` for auto-increment IDs
- `productId` (String) is the query key for `findByProductId()`
- Price stored as total (quantity × unit_price) after each operation
- No separate unit price field — recalculate from PRODUCTS map when needed

### Repository Queries
Spring Data JPA automatically implements:
- `findByProductId()` → returns single item or null
- `deleteByProductId()` → removes item

To add custom queries, use `@Query` annotation in `CartItemRepository`.

## Package Structure

```
com.demo.shopping_cart_mcp/
├── ShoppingCartMcpApplication.java    # Boot entry, Bean registration
├── entity/CartItem.java                # JPA @Entity
├── repository/CartItemRepository.java  # Spring Data interface
└── tools/ShoppingCartMcpService.java   # @Service with @Tool methods
```

**Naming convention**: Underscores in package name (`shopping_cart_mcp`) because hyphens are invalid in Java package names.

## Testing & Validation

- **Basic test**: `ShoppingCartMcpApplicationTests` only verifies context loads
- **Manual tool testing**: Start server and invoke tools via MCP client (e.g., Claude MCP client)
- **Integration gaps**: No repository tests or tool call simulation tests; add these for new features
- **H2 embedded**: Tests use H2 database (in-memory) and don't require PostgreSQL to run

## Dependencies & Versions

- **Spring Boot**: 3.5.14 (parent POM)
- **Spring AI**: 1.1.6 (MCP server starter)
- **Spring Data JPA**: (auto-managed by Boot parent)
- **PostgreSQL Driver**: (auto-managed by Boot parent)
- **Lombok**: Optional, for annotation processing only
- **JUnit 5**: Via Spring Boot starter-test

Check `pom.xml` for full dependency tree.

## Common Pitfalls & Gotchas

1. **Package naming**: Always use `com.demo.shopping_cart_mcp` (underscores), not hyphens
2. **PostgreSQL connection**: Ensure PostgreSQL is running and accessible at configured URL
3. **Null check in tools**: `getItemCart()` returns NPE if item not found; add null checks when adding similar tools
4. **Tool descriptions**: Keep them concise; AI clients use these to decide tool eligibility
5. **MCP Server type**: Configured as `SYNC` — changing to `ASYNC` requires framework version bump
6. **Logging pattern incomplete**: `application.yaml` line 22 is cut off; restore if logging issues arise
7. **JPA Entity annotations**: Use `jakarta.persistence` (not `javax.persistence`) for Spring Boot 3.5.14

## Relevant Files for Common Tasks

| Task | File(s) |
|------|---------|
| Add new tool | `tools/ShoppingCartMcpService.java` |
| Add new product | `tools/ShoppingCartMcpService.java` (PRODUCTS map) |
| Change database config | `src/main/resources/application.yaml` |
| Modify cart item schema | `entity/CartItem.java` |
| Add custom queries | `repository/CartItemRepository.java` |
| Debug boot process | `ShoppingCartMcpApplication.java`, `pom.xml` |

