-- ==========================================
-- 📦 BÁCH HÓA ONLINE DATABASE
-- ==========================================
DROP DATABASE bachhoa;
CREATE DATABASE IF NOT EXISTS bachhoa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bachhoa;

-- =========================
-- 1️⃣ BẢNG USERS
-- =========================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(150) UNIQUE,
  role ENUM('USER','ADMIN') DEFAULT 'USER',
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO users (name, email, role) VALUES
('Alice', 'alice@test.com', 'USER'),
('Bob', 'bob@test.com', 'ADMIN');

-- =========================
-- 2️⃣ BẢNG PRODUCTS
-- =========================


DROP TABLE IF EXISTS products;
CREATE TABLE products (
  productId INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  basePrice DECIMAL(10,2) NOT NULL,
  categoryId INT,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO products (name, description, basePrice, categoryId) VALUES
('Táo đỏ Fuji', 'Táo đỏ nhập khẩu giòn ngọt, hương vị tươi mát.', 45000, 1),
('Cam Mỹ Sunkist', 'Cam vàng Mỹ ngọt, mọng nước và thơm.', 55000, 1),
('Chuối tiêu', 'Chuối tiêu sạch, thơm tự nhiên, bổ dưỡng.', 25000, 2),
('Dưa hấu không hạt', 'Dưa hấu đỏ ngọt thanh, không hạt.', 30000, 2);

-- =========================
-- 3️⃣ BẢNG PRODUCT IMAGES
-- =========================

DROP TABLE IF EXISTS productimages;
CREATE TABLE productimages (
  imageId INT AUTO_INCREMENT PRIMARY KEY,
  productId INT NOT NULL,
  imageUrl VARCHAR(500),
  isMain BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE
);

INSERT INTO productimages (productId, imageUrl, isMain) VALUES
(1, 'https://cdn.tgdd.vn/Products/Images/8788/282550/bhx/tao-do-fuji-tui-1kg-202212271118435261.jpg', TRUE),
(2, 'https://cdn.tgdd.vn/Products/Images/8788/282545/bhx/cam-my-sunkist-tui-1kg-202212271122395153.jpg', TRUE),
(3, 'https://cdn.tgdd.vn/Products/Images/8788/282546/bhx/chuoi-tieu-1kg-202212271119235246.jpg', TRUE),
(4, 'https://cdn.tgdd.vn/Products/Images/8788/285046/bhx/dua-hau-khong-hat-1kg-20240102090905.jpg', TRUE);

-- =========================
-- 4️⃣ BẢNG PRODUCT VARIANTS
-- =========================

DROP TABLE IF EXISTS productvariants;
CREATE TABLE productvariants (
  variantId INT AUTO_INCREMENT PRIMARY KEY,
  productId INT NOT NULL,
  attributes JSON,
  price DECIMAL(10,2),
  FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE
);

INSERT INTO productvariants (productId, attributes, price) VALUES
(1, '{"size":"1kg"}', 45000),
(1, '{"size":"2kg"}', 88000),
(2, '{"size":"1kg"}', 55000),
(3, '{"size":"1kg"}', 25000),
(4, '{"size":"1 quả (2kg)"}', 60000);

-- =========================
-- 5️⃣ BẢNG REVIEWS
-- =========================

DROP TABLE IF EXISTS reviews;
CREATE TABLE reviews (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  user_id INT NOT NULL,
  rating INT CHECK (rating BETWEEN 1 AND 5),
  comment TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(productId) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Dữ liệu mẫu review
INSERT INTO reviews (product_id, user_id, rating, comment) VALUES
(1, 1, 5, 'Táo rất giòn và ngọt, giao hàng nhanh!'),
(1, 2, 4, 'Hàng tươi nhưng hơi ít nước.'),
(2, 1, 5, 'Cam Mỹ thơm ngon, đáng tiền.'),
(3, 2, 3, 'Chuối ổn, nhưng chưa chín kỹ.'),
(4, 1, 5, 'Dưa hấu rất ngon và ngọt.');

-- =========================
-- 6️⃣ VIEW: AVG RATING (tuỳ chọn)
-- =========================

DROP VIEW IF EXISTS view_product_avg_rating;
CREATE VIEW view_product_avg_rating AS
SELECT 
  p.productId,
  p.name,
  ROUND(AVG(r.rating),1) AS avgRating,
  COUNT(r.id) AS totalReviews
FROM products p
LEFT JOIN reviews r ON p.productId = r.product_id
GROUP BY p.productId;

-- =========================
-- ✅ TEST
-- =========================
-- Kiểm tra sản phẩm + điểm trung bình:
-- SELECT * FROM view_product_avg_rating;
--
-- Kiểm tra review của 1 sản phẩm:
-- SELECT * FROM reviews WHERE product_id = 1;
--
-- Kiểm tra API:
--  GET  http://localhost:8080/ecommerce-hoa/api/products
--  GET  http://localhost:8080/ecommerce-hoa/api/reviews?productId=1
