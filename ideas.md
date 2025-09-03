# E-Commerce Website (Next.js + Prisma + SQLite)

## 1. Frontend (UI/UX)

### Thiết kế UI/UX
- **Framework**: Next.js + React Server Components

### Layout
- Root layout (`layout.tsx`)
- Navbar, Footer, Sidebar
- Grid/List cho sản phẩm

### Page chính
- `/` → Trang chủ (banner, sản phẩm nổi bật)
- `/products` → Danh sách sản phẩm
- `/product/[id]` → Chi tiết sản phẩm
- `/cart` → Giỏ hàng
- `/checkout` → Thanh toán
- `/orders` → Lịch sử đơn hàng
- `/signin` + `/signup`
- `/admin/*` → Dashboard quản lý sản phẩm, user, orders

### Component tái sử dụng
- ProductCard, ProductGrid
- Button, Input, Modal (shadcn/ui)
- CartWidget, OrderTable

---

## 2. Backend (API Routes)

### API Products
- `GET /api/products` → list
- `POST /api/products` → admin add
- `PATCH /api/products/:id`
- `DELETE /api/products/:id`

### API Cart
- `GET /api/cart`
- `POST /api/cart` → add item
- `PATCH /api/cart` → update quantity
- `DELETE /api/cart` → remove item

### API Orders
- `POST /api/orders` → tạo từ giỏ hàng
- `GET /api/orders` → của user hiện tại
- `PATCH /api/orders/:id` → admin update status

### API Auth (NextAuth)
- `/api/auth/[...nextauth]`

### Công nghệ
- Next.js App Router API routes
- Zod (input validation)
- Prisma ORM

---

## 3. Database (Prisma + SQLite)

### Cấu hình
- Prisma Client singleton → dùng chung trong project

### Schema
- **User**: role, email, password hash
- **Product**: tên, giá, stock, imageUrl, desc
- **Cart**, **CartItem**
- **Order**, **OrderItem**
- **Account**, **Session**, **VerificationToken** (cho NextAuth)

### Migration
```bash
pnpm prisma migrate dev --name init
````

### Seed data

* Thêm 5–10 sản phẩm demo vào `prisma/seed.ts`

### Quan hệ

* 1–n: User – Orders
* 1–1: User – Cart
* n–n: Product – CartItem

---

## 4. Authentication & Authorization

### NextAuth cấu hình

* Provider: Credentials (email/password)
* Adapter: PrismaAdapter
* Session: JWT

### Hash password

* Bcrypt (server-side)

### RBAC

* Role: USER, ADMIN
* Middleware bảo vệ `/admin/*`

### Client Auth

* Hook: `useSession()`
* Component: `SignIn`, `SignOut`, `ProfileMenu`

---

## 5. Giỏ hàng & Thanh toán

### Cart

* API + frontend hook
* Hiển thị badge (số lượng sản phẩm)

### Stripe Integration

* Checkout Session
* Webhook cập nhật Order khi thanh toán thành công

### VNPay Integration (demo flow)

* Tạo URL redirect
* Xử lý callback (IPN) → cập nhật trạng thái đơn hàng

---

## 6. Triển khai (Deployment)

* **Frontend + API** → Vercel
* **Database** → Railway / Supabase / Neon (Postgres cloud)

### ENV Secrets

```env
DATABASE_URL="file:./dev.db"
NEXTAUTH_SECRET=xxxx
STRIPE_SECRET_KEY=xxxx
NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY=xxxx
STRIPE_WEBHOOK_SECRET=xxxx
NEXT_PUBLIC_APP_URL=https://example.com
```

### Domain

* Custom domain (Cloudflare DNS)
* HTTPS auto (Vercel/Cloudflare)

---

## 7. Bổ sung để hoàn thiện hơn

### Test

* Unit test: Jest / Vitest
* API test: Supertest
* E2E: Playwright

### Performance

* Image Optimization (`next/image`)
* Caching (react-query hoặc swr)
* Lazy load component

### SEO

* Metadata API của Next.js
* OpenGraph tags
* `sitemap.xml`, `robots.txt`

### Analytics

* Vercel Analytics
* Google Analytics (GA4)

### CI/CD

* GitHub Actions: lint + test + deploy

### Bảo mật

* Helmet headers (middleware Next.js)
* Rate limiting (Upstash Redis)
* Input validation bằng Zod

### Logging & Monitoring

* Sentry (error tracking)
* Vercel Logs / Logtail

### Email & Notifications

* Nodemailer / Resend
* Email khi order thành công

### Admin Dashboard

* Quản lý sản phẩm
* Quản lý đơn hàng
* Quản lý user

