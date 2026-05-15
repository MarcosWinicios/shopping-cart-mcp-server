# Shopping Cart MCP Server

Servidor MCP (Model Context Protocol) construído com Spring Boot que expõe operações de carrinho de compras como ferramentas acessíveis por IA, utilizando PostgreSQL como banco de dados.

## 🚀 Quick Start

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 12+ (ou use Docker)

### 1. Iniciar PostgreSQL

**Opção A: Docker**
```bash
docker run -d \
  --name shopping-cart-postgres \
  -p 5432:5432 \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=shopping_cart_mcp \
  postgres:latest
```

**Opção B: PostgreSQL Local**
```bash
createdb -U postgres -h localhost shopping_cart_mcp
```

### 2. Build do Projeto

```bash
./mvnw clean package
```

### 3. Executar o Servidor MCP

```bash
java -jar target/shopping-cart-server.jar
# OU
./mvnw spring-boot:run
```

O servidor aguardará conexões MCP no stdin/stdout.

## 📊 Arquitetura

```
AI Client ←→ MCP Server (stdio) ←→ Spring Boot App ←→ PostgreSQL
```

O servidor **NÃO** é uma REST API - é um processo stdio-based que comunica com clientes MCP.

### Componentes

- **ShoppingCartMcpApplication**: Configuração boot e registro de ferramentas
- **ShoppingCartMcpService**: Serviço com métodos anotados com `@Tool`
- **CartItem**: Entidade JPA persistida em PostgreSQL
- **CartItemRepository**: Spring Data JPA repository

## 🛠️ Ferramentas Disponíveis

- **addToCart** - Adiciona produto ao carrinho
- **removeFromCart** - Remove produto do carrinho
- **getItemCart** - Obtém detalhes de um item
- **getAllItemsCart** - Lista todos os itens do carrinho
- **getCartTotal** - Calcula o total do carrinho

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/demo/shopping_cart_mcp/
│   │   ├── ShoppingCartMcpApplication.java      # Configuração Boot
│   │   ├── entity/CartItem.java                 # Entidade JPA
│   │   ├── repository/CartItemRepository.java   # Spring Data JPA
│   │   └── tools/ShoppingCartMcpService.java    # Serviço com @Tool
│   └── resources/application.yaml               # Configuração
└── test/
    ├── java/com/demo/shopping_cart_mcp/
    │   └── ShoppingCartMcpApplicationTests.java
    └── resources/application-test.yaml
```

## ⚙️ Configuração

A configuração PostgreSQL está em `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shopping_cart_mcp
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
```

Hibernate cria/atualiza automaticamente as tabelas baseado nas entidades.

## 🧪 Testes

```bash
# Rodar testes (usa H2 em memória, não requer PostgreSQL)
./mvnw test

# Build completo com testes
./mvnw clean package
```

## 📝 Adicionando Produtos

Edite o mapa `PRODUCTS` em `ShoppingCartMcpService.java`:

```java
private static final Map<String, Double> PRODUCTS = Map.of(
    "iPhone", 79999.0,
    "MacBook Air", 129999.0,
    "Boat Airdrops", 1999.0
);
```

## 🆕 Adicionando Ferramentas

1. Adicione um método em `ShoppingCartMcpService` com `@Tool` e `@ToolParam`:

```java
@Tool(name = "myTool", description = "Descrição da ferramenta")
public String myTool(@ToolParam String param1, @ToolParam int param2) {
    // implementação
    return "resultado";
}
```

2. Rebuild: `./mvnw clean package`

## 🔗 Dependências

- Spring Boot 3.5.14
- Spring AI MCP 1.1.6
- Spring Data JPA 3.5.11
- PostgreSQL Driver (runtime)
- H2 (test scope)
- Lombok (optional)

## 📚 Referência

Para mais detalhes arquiteturais e padrões de desenvolvimento, veja [AGENTS.md](AGENTS.md).
