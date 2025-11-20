
# Microservice PurchaseService LevelUp (com.levelup.backend)

Endpoints:
- POST /api/orders/checkout
- GET  /api/orders/{orderId}
- GET  /api/orders/user/{userId}

Comparte base de datos con el microservicio de carrito.
- Tablas leídas: `carts`, `cart_items`
- Tablas escritas: `orders`, `order_items`

Ejecuta:
```
mvn spring-boot:run
```
