# Giramundo Rock Store

Template de loja virtual em **Java + Spring Boot**, com front-end estático em HTML, CSS e JavaScript.

O projeto foi preparado para funcionar como vitrine inicial, com carrinho 100% em JavaScript e envio do pedido completo para WhatsApp.

## Funcionalidades incluídas

- Abas/seções: **Home**, **Sobre**, **Loja** e **Contato**.
- Layout escuro com estética rock in roll, baseado na identidade visual da Giramundo.
- Lista de produtos mockada no arquivo `app.js`.
- Carrinho em JavaScript com persistência em `localStorage`.
- Adicionar produto ao carrinho.
- Aumentar ou reduzir quantidade do produto.
- Remover item individual.
- Limpar carrinho.
- Cálculo automático do total.
- Formulário de endereço dentro do carrinho.
- Botão **Solicitar pedido** bloqueado até existir produto no carrinho e endereço obrigatório preenchido.
- Montagem automática da mensagem do pedido.
- Envio do pedido para WhatsApp via `https://wa.me`.

## Estrutura do projeto

```text
giramundo-rock-store/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── br/com/giramundo/store/
        │       └── GiramundoStoreApplication.java
        └── resources/
            ├── application.properties
            └── static/
                ├── index.html
                └── assets/
                    ├── css/
                    │   └── styles.css
                    ├── img/
                    │   └── logo-giramundo.png
                    └── js/
                        └── app.js
```

## Como rodar

Pré-requisitos:

- Java 21
- Maven 3.9+

Execute:

```bash
mvn spring-boot:run
```

Acesse:

```text
http://localhost:8080
```

## Configurar o WhatsApp da loja

Abra o arquivo:

```text
src/main/resources/static/assets/js/app.js
```

Altere:

```javascript
const CONFIG = {
    whatsappNumber: "5585999999999",
    companyName: "Giramundo"
};
```

Use o número no formato:

```text
DDI + DDD + número, somente dígitos
```

Exemplo:

```text
5585999999999
```

## Onde alterar os produtos

No arquivo:

```text
src/main/resources/static/assets/js/app.js
```

Edite o array `products`:

```javascript
const products = [
    {
        id: 1,
        name: "Camiseta Eagle Road",
        description: "Camiseta premium com estampa frontal inspirada na águia Giramundo.",
        price: 89.9,
        badge: "01"
    }
];
```

Quando o backend for implementado, você pode substituir esse array por uma chamada para API, por exemplo:

```javascript
fetch("/api/products")
    .then(response => response.json())
    .then(data => {
        products = data;
        renderProducts();
    });
```

## Sugestão de evolução para backend

Endpoints recomendados para implementar depois:

```text
GET    /api/products
GET    /api/products/{id}
POST   /api/orders
GET    /api/orders/{id}
```

Modelo inicial de produto:

```java
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean active;
}
```

Modelo inicial de pedido:

```java
public class Order {
    private Long id;
    private String customerName;
    private String customerPhone;
    private String street;
    private String number;
    private String district;
    private String city;
    private String state;
    private String complement;
    private BigDecimal total;
    private List<OrderItem> items;
}
```

## Observações técnicas

- O carrinho é salvo em `localStorage` usando a chave `giramundo_cart`.
- O envio para WhatsApp é feito no front-end, sem backend.
- O botão de pedido depende de duas validações:
  - Carrinho com pelo menos um item.
  - Campos obrigatórios de endereço preenchidos.
- O projeto já está pronto para receber APIs Spring posteriormente.
