# Shopping Cart MCP Server

Servidor MCP (Model Context Protocol) construído com Spring Boot que expõe operações de carrinho de compras como ferramentas acessíveis por IA, utilizando PostgreSQL como banco de dados.

## 🚀 Quick Start

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 12+

### 1. Iniciar PostgreSQL

Crie o banco de dados localmente:
```bash
createdb -U postgres -h localhost shopping-cart-mcp
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
AI Client ←→ MCP Server ←→ Spring Boot App ←→ PostgreSQL
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
│   │   ├── ShoppingCartMcpApplication.java      # Configuração Boot e registro de ferramentas
│   │   ├── entity/CartItem.java                 # Entidade JPA
│   │   ├── repository/CartItemRepository.java   # Spring Data JPA
│   │   └── tools/ShoppingCartMcpService.java    # Serviço com métodos @Tool
│   └── resources/application.yaml               # Configuração
└── test/
    └── java/com/demo/shopping_cart_mcp/
        └── ShoppingCartMcpApplicationTests.java # Teste básico de contexto
```

Hibernate cria/atualiza automaticamente as tabelas baseado nas entidades.

## 🧪 Testes

```bash
# Rodar testes
./mvnw test

# Build completo com testes
./mvnw clean package
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

## 🌐 Usando MCP em IDEs

Este servidor MCP pode ser integrado a uma IDE adicionando-o com a seguinte configuração


```json
{
  "mcpServers": {
    "shopping-cart-server": {
      "url": "http://localhost:8080/mcp"
    }
  }
}
```

### Exemplo de Uso em Chat IA

```
"Adicione 2 iPhones e 1 MacBook Air ao carrinho de compras"
```

O assistente IA invocará automaticamente as ferramentas disponíveis para processar o comando.

## 🔗 Dependências

- Spring Boot 3.5.14
- Spring AI MCP 1.1.6
- Spring Data JPA 3.5.11
- PostgreSQL Driver (runtime)
- Lombok (optional)

## 📚 Referência

Para mais detalhes arquiteturais e padrões de desenvolvimento, veja [AGENTS.md](AGENTS.md).
