# Bách Hóa Backend (Servlet + JPA)

> Tomcat 9 + Java 17 + JPA 2.2 (Hibernate) + MySQL 8.  
> WAR name is `bachhoa.war` and context path is `/bachhoa` by default.

## How to run (dev)

1. Install **JDK 17**, **Maven 3.9+**, **MySQL 8**.
2. Create DB and user (or use root) and update `src/main/resources/META-INF/persistence.xml`:
   - `javax.persistence.jdbc.url` (default: `jdbc:mysql://localhost:3306/bachhoa?...`)
   - `user`, `password`
3. Build:
   ```bash
   mvn clean package
   ```
4. Deploy the generated `target/bachhoa.war` to **Tomcat 9** (`webapps/`).
5. Verify:
   - `GET http://localhost:8080/bachhoa/api/health` → `{ "status": "ok" }`
   - `GET http://localhost:8080/bachhoa/api/products`

### Vite frontend proxy (example)

If FE runs on `http://localhost:5173`, set Vite proxy target to `http://localhost:8080/bachhoa` and call `/api/...` from FE.

```ts
// vite.config.ts (server.proxy)
'/api': {
  target: 'http://localhost:8080/bachhoa',
  changeOrigin: true,
  secure: false,
}
```

## Project layout

```
src/
  main/
    java/vn/bachhoa/...
      entities/      # JPA entities (Users, Products, ProductVariants, ...)
      dao/           # Simple DAO layer (ProductDAO demo)
      web/           # Servlets (ProductServlet, HealthServlet)
      web/filters/   # CORS
      util/          # JPAUtil, JsonUtil
    resources/META-INF/persistence.xml
    webapp/WEB-INF/web.xml
sql/
  schema.sql         # optional seed
```

## Next steps per assignment

- **Products & Variants (search/filter/detail):** extend `ProductServlet` and `ProductDAO` (owner: Ái Nguyên / Ngọc Thùy / Việt Hoa).
- **Cart & CartItems:** create `CartServlet` (`/api/cart`) and DAO (owner: Lê Ngô Thanh Hoa).
- **Orders & Status flow:** create `OrderServlet` (`/api/orders`) (owner: Thanh Trang).
- **Auth (register/login/reset):** add `AuthServlet` and basic password hashing (owner: Hồng Nhung).
- **Admin CRUD (Products/Variants):** add `AdminProductServlet` (owner: Phước Thịnh).
- **Reviews:** add `ReviewServlet` (owner: Kiều Oanh).

> Shipment class is intentionally omitted as per team note; track shipping status via `orders.status`.

## Notes

- JPA auto DDL is `update` for dev; switch to `validate` in production.
- `ProductVariant.attributes` is stored as JSON string for flexibility.
- Add indexes/migrations later if needed.
- Keep API base path **/api/** to match FE sample.
