# Market Mesh

An e-commerce platform (Flipkart/Amazon/Meesho style) built with **Java Spring Boot** and **MySQL**.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot |
| Database | MySQL |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security + JWT (jjwt) |
| Build Tool | Maven |
| Boilerplate reduction | Lombok |
| Validation | Jakarta Bean Validation |

---

## Package Structure

Organized **by feature**, not by layer — each business domain owns its entity, repository, service, controller, and DTOs together.

```
com.marketmesh
 ├── user            User entity, Role enum, UserRepository
 ├── address         Address entity, AddressRepository
 ├── category        Category entity (self-referencing), Repository, Service, Controller, dto/
 ├── product         Product, ProductImage entities, ProductRepository
 ├── cart            Cart, CartItem entities, repositories
 ├── order           Order, OrderItem entities, OrderStatus enum, repositories
 ├── payment         Payment entity, PaymentStatus enum, PaymentRepository
 ├── auth            AuthService, AuthController, dto/ (Register & Login request/response)
 ├── common          ApiResponse<T> - generic response wrapper
 └── config          SecurityConfig, JwtUtil, JwtAuthFilter
```

---

## Database Schema

9 core tables, all auto-created by Hibernate (`ddl-auto=update`) from the `@Entity` classes:

| Table | Purpose |
|---|---|
| `users` | Registered users (customers + admins), role-based (`USER` / `ADMIN`) |
| `addresses` | Saved shipping addresses per user |
| `categories` | Product categories, self-referencing (`parent_id`) for subcategories |
| `products` | Product catalog, linked to a category |
| `product_images` | Multiple images per product, with display order |
| `carts` | One active cart per user |
| `cart_items` | Line items in a cart, with price snapshot at add-time |
| `orders` | Placed orders (table name `orders`, since `order` is a reserved SQL keyword) |
| `order_items` | Frozen snapshot of product + price + quantity at order time |
| `payments` | 1:1 with orders — payment method, status, transaction ID |

**Key design decisions:**
- **Cart vs Order separation** — Cart/CartItem is "live" (prices can be re-checked), Order/OrderItem is a permanent frozen snapshot. Once placed, an order's contents never change even if the product is later edited or deleted.
- **Self-referencing Category** — subcategories reference their parent category in the same table (e.g. Electronics → Mobiles → Smartphones).
- **`@JsonIgnore` on back-references** — prevents infinite loops when serializing bidirectional relationships to JSON (e.g. `Address → User → Address...`).

---

## Authentication & Authorization

**JWT-based, stateless authentication.**

### How it works
1. User registers via `POST /api/auth/register` — password is hashed with `BCryptPasswordEncoder` before storage.
2. User logs in via `POST /api/auth/login` — on success, receives a signed JWT containing their email and role.
3. For any protected endpoint, the client sends the token in the header:
   ```
   Authorization: Bearer <token>
   ```
4. `JwtAuthFilter` intercepts every request, validates the token, and sets the authenticated user (with role) into Spring Security's context.

### Role-based access rules
| Route | Access |
|---|---|
| `/api/auth/**` | Public (no token required) |
| `GET /api/categories/**`, `GET /api/products/**` | Public (anyone can browse) |
| `POST/PUT/DELETE /api/categories/**`, `/api/products/**` | `ADMIN` only |
| Everything else (cart, orders, etc.) | Any authenticated `USER` |

To promote a user to admin (no admin UI yet — do this directly in MySQL):
```sql
UPDATE users SET role='ADMIN' WHERE email='someone@example.com';
```
The user must **log in again** afterward, since the role is embedded in the JWT at login time — an old token still carries the old role.

---

## API Response Format

Every endpoint returns a consistent wrapper shape via `ApiResponse<T>`:
```json
{
  "success": true,
  "message": "Login successful",
  "data": { }
}
```

---

## API Endpoints (built so far)

### Auth
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login, returns JWT |

### Category
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/categories` | ADMIN | Create category |
| GET | `/api/categories` | Public | List top-level categories (with nested subcategories) |
| GET | `/api/categories/{id}` | Public | Get one category |
| PUT | `/api/categories/{id}` | ADMIN | Update category |
| DELETE | `/api/categories/{id}` | ADMIN | Delete category |

### Product
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/products` | ADMIN | Create product (with image URLs) |
| GET | `/api/products` | Public | List all products |
| GET | `/api/products?categoryId={id}` | Public | Filter products by category |
| GET | `/api/products?keyword={text}` | Public | Search products by name |
| GET | `/api/products/{id}` | Public | Get one product |
| PUT | `/api/products/{id}` | ADMIN | Update product (images fully replaced) |
| DELETE | `/api/products/{id}` | ADMIN | Delete product |

---

## Setup Instructions

1. **Clone/open the project** in VS Code (Java Extension Pack + Spring Boot Extension Pack + Lombok Annotations Support installed).

2. **Create the database** in MySQL:
   ```sql
   CREATE DATABASE market_mesh;
   ```

3. **Configure `src/main/resources/application.properties`:**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/market_mesh?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   jwt.secret=ThisIsASecretKeyForMarketMeshChangeItLaterInProduction123456
   jwt.expiration=86400000

   server.port=8080
   ```

4. **Run the app:**
   ```bash
   mvn clean spring-boot:run
   ```

5. **Verify:** check console for `HikariPool-1 - Start completed` (DB connected) and no red stack traces.

---

## What's NOT built yet (upcoming work)

- Cart Service + Controller (add/remove/update items)
- Order Service + Controller (Cart → Order conversion, stock deduction)
- Payment integration (mock gateway to start)
- Global exception handler (`@ControllerAdvice`) — currently errors return raw `500`/`403` without clean JSON error bodies
- Address CRUD endpoints
- Pagination & search/filtering on product listing
- Docker containerization *(explicitly deferred for now)*

---

## Notes for Future Development

- Passwords are **never** returned in any API response — only hashes are stored, and even those never leave the server.
- `discountPrice` on Product is nullable — absence means "no discount," not zero.
- All DTOs are hand-written (no auto-mapping library like MapStruct) — entity ↔ DTO conversion is done manually inside each Service class (see `CategoryService.toResponse()` / `ProductService.toResponse()` as the pattern to follow for Cart, Order next).
- Product images are stored as plain URL strings (no file upload/cloud storage) — matches the earlier decision to keep image handling simple for v1.
- `ProductService.update()` fully replaces the image list on every update rather than diffing add/remove — simplest correct behavior for now; revisit only if the frontend needs partial image edits.