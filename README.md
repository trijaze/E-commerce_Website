Quá ổn. Dưới đây là **README.md** đã chỉnh cho **monorepo** (cả frontend + backend), giữ nguyên phần bạn đưa và bổ sung phần BE/DB. Chỉ cần **dán đè** vào `README.md` ở gốc repo.

````md
# Bách Hóa Online – E-commerce Website

Monorepo gồm:
- **bachhoa-frontend**: React + Vite
- **bachhoa-backend**: Java Servlet + JPA (Hibernate 5) chạy trên **Tomcat 9 / JDK 17**
- **db/**: script SQL khởi tạo dữ liệu đầy đủ (sản phẩm, ảnh, biến thể, tồn kho, review, view…)

---

## ⚙️ Yêu cầu

- **Frontend**: Node.js ≥ 20, npm ≥ 10  
- **Backend**: JDK 17, Tomcat 9, MySQL 8.x

---

## 🗄️ Cài database

1. Mở MySQL Workbench/CLI và chạy file: **`db/full-reset.sql`**  
   (reset DB `bachhoa`, tạo bảng, seed dữ liệu, view, procedure…)
2. Kiểm tra nhanh:
   ```sql
   SELECT COUNT(*) FROM products;
   SELECT productId, variantId, JSON_EXTRACT(attributes,'$.size') size, price
   FROM productvariants ORDER BY productId, variantId;
````

> File `db/full-reset.sql` đã bao gồm: `products`, `productimages` (≥2 ảnh/SP), `productvariants` (nhiều size), `inventories` (tồn kho), `reviews`, các view `view_product_avg_rating`, `view_variant_stock`, `view_product_stock`.

---

## 🖥️ Backend (Tomcat 9 + JDK 17)

1. Tạo cấu hình local:

   ```bash
   cd bachhoa-backend/src/main/resources
   cp application.properties.example application.properties
   # sửa username/password/url cho máy mình
   ```

   > `application.properties` đã **ignore**, KHÔNG commit.

2. Chạy backend

   * **Eclipse/Tomcat 9**: Add project `bachhoa-backend` vào server → Start.
   * **Maven**:

     ```bash
     cd bachhoa-backend
     mvn clean package
     # deploy target/*.war vào Tomcat 9 (webapps) rồi start server
     ```

3. API chính

   * `GET /api/products?q=&limit=&offset=` – danh sách
   * `GET /api/products/{id}` – chi tiết (images, variants, minPrice)
   * `GET /api/products/{id}/related` – gợi ý sản phẩm liên quan

---

## 🚀 Frontend (Vite)

1. Clone repo:

   ```bash
   git clone <URL_REPO>
   cd E-commerce_Website
   ```

2. Cài dependencies:

   ```bash
   cd bachhoa-frontend
   npm install
   ```

3. Chạy dev:

   ```bash
   npm run dev
   ```

   👉 Mở [http://localhost:5173](http://localhost:5173)
   (Proxy `/api` → `http://localhost:8080`)

### 🏗️ Build production

```bash
npm run build
```

Kết quả nằm trong thư mục `dist/`.

Chạy thử build:

```bash
npm run preview
```

---

## 📦 Công nghệ chính

* React + Vite
* Tailwind CSS
* Redux Toolkit
* React Router DOM
* React Toastify

### Các thư viện (đã dùng)

```bash
npm install react-redux
npm install --save-dev @types/react-redux
npm install @reduxjs/toolkit
```

---

## 🛒 Giao diện Sản phẩm

<img width="1504" height="763" alt="image" src="https://github.com/user-attachments/assets/b07f5687-6dcb-4be9-990c-042476b22331" />

## ⭐ Giao diện Review

<img width="1466" height="703" alt="image" src="https://github.com/user-attachments/assets/aaeeaa34-d646-4f0a-8791-6a5a98cb9bb7" />

## 👤 Giao diện User Profile

> Chưa có đăng ký/đăng nhập nên mock sẵn ở `authSlice.tsx`:

```ts
const initialState: AuthState = {
  user: {
    id: '1',
    name: 'Trịnh Trần Phương Tuấn',
    email: 'ttpt@test.com',
    role: 'USER',
    createdAt: '2024-10-09T00:00:00Z'
  },
  loading: false,
  error: null
};
```

<img width="1755" height="1180" alt="image" src="https://github.com/user-attachments/assets/c75c7548-0498-4d05-a142-f34da8edfcfb" />

---

## 📁 Cấu trúc repo

```
E-commerce_Website/
├─ bachhoa-backend/
│  ├─ src/main/java/...
│  ├─ src/main/resources/
│  │  ├─ application.properties.example
│  │  └─ (application.properties — local, không commit)
│  └─ pom.xml
├─ bachhoa-frontend/
│  ├─ src/...
│  └─ package.json
├─ db/
│  └─ full-reset.sql
├─ .gitignore
└─ README.md
```

---

## 🔒 Lưu ý bảo mật / commit

* **Không commit secrets** (`application.properties` đã ignore).
* **Không commit rác IDE/build**: `.metadata/`, `Servers/`, `target/`, `node_modules/`, `dist/`…
* **Nên commit lockfile** (`package-lock.json`) để build ổn định.

---

## 📝 Góp ý / dev notes

* Backend đã tránh lỗi `MultipleBagFetchException` bằng cách **tách truy vấn** (product/images/variants).
* Trang chi tiết cho phép **chọn biến thể (0.5kg/1kg/2kg/3kg…)** và **số lượng**; giá thay đổi theo biến thể.
* Có thể mở rộng hiển thị tồn kho theo biến thể từ `view_variant_stock.available`.

````
