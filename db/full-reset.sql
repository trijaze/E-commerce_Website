-- ==========================================
-- 📦 BÁCH HÓA ONLINE DATABASE – FULL RESET
-- Stack: MySQL 8.x (utf8mb4)
-- ==========================================

/* Reset database */
DROP DATABASE IF EXISTS bachhoa;
CREATE DATABASE bachhoa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bachhoa;

SET NAMES utf8mb4;
SET time_zone = '+07:00';

-- ------------------------------------------
-- Drop views first (if any)
-- ------------------------------------------
DROP VIEW IF EXISTS view_product_stock;
DROP VIEW IF EXISTS view_variant_stock;
DROP VIEW IF EXISTS view_product_avg_rating;

-- ------------------------------------------
-- Drop tables (order matters)
-- ------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS inventory_logs;
DROP TABLE IF EXISTS inventories;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS productvariants;
DROP TABLE IF EXISTS productimages;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- 1) USERS
-- =========================
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(150) UNIQUE,
  role ENUM('USER','ADMIN') DEFAULT 'USER',
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (name, email, role) VALUES
('Alice', 'alice@test.com', 'USER'),
('Bob',   'bob@test.com',   'ADMIN');

-- =========================
-- 2) PRODUCTS
-- =========================
CREATE TABLE products (
  productId  INT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(255) NOT NULL,
  description TEXT,
  basePrice  DECIMAL(10,2) NOT NULL,
  categoryId INT,
  createdAt  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO products (name, description, basePrice, categoryId) VALUES
('Táo đỏ Fuji',         'Táo đỏ nhập khẩu giòn ngọt, hương vị tươi mát.', 45000, 1),
('Cam Mỹ Sunkist',      'Cam vàng Mỹ ngọt, mọng nước và thơm.',           55000, 1),
('Chuối tiêu',          'Chuối tiêu sạch, thơm tự nhiên, bổ dưỡng.',      25000, 2),
('Dưa hấu không hạt',   'Dưa hấu đỏ ngọt thanh, không hạt.',              30000, 2);

-- =========================
-- 3) PRODUCT IMAGES (≥2 ảnh / sản phẩm)
-- =========================
CREATE TABLE productimages (
  imageId   INT AUTO_INCREMENT PRIMARY KEY,
  productId INT NOT NULL,
  imageUrl  VARCHAR(500) NOT NULL,
  isMain    BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Ảnh chính (isMain = TRUE) – giữ link đúng tên SP
INSERT INTO productimages (productId, imageUrl, isMain) VALUES
(1, 'https://cdn.tgdd.vn/Products/Images/8788/282550/bhx/tao-do-fuji-tui-1kg-202212271118435261.jpg', TRUE),
(2, 'https://cdn.tgdd.vn/Products/Images/8788/282545/bhx/cam-my-sunkist-tui-1kg-202212271122395153.jpg', TRUE),
(3, 'https://cdn.tgdd.vn/Products/Images/8788/282546/bhx/chuoi-tieu-1kg-202212271119235246.jpg', TRUE),
(4, 'https://cdn.tgdd.vn/Products/Images/8788/285046/bhx/dua-hau-khong-hat-1kg-20240102090905.jpg', TRUE);

-- Ảnh phụ (isMain = FALSE) – ảnh minh họa đúng loại
INSERT INTO productimages (productId, imageUrl, isMain) VALUES
(1, 'https://images.unsplash.com/photo-1567306226416-28f0efdc88ce', FALSE), -- Táo
(2, 'https://images.unsplash.com/photo-1547514701-9cdcb1f59486',   FALSE), -- Cam
(3, 'https://images.unsplash.com/photo-1571772996211-2f02b77fc5f8', FALSE), -- Chuối
(4, 'https://images.unsplash.com/photo-1598966733531-9b52ad2aaf4d', FALSE); -- Dưa hấu

-- =========================
-- 4) PRODUCT VARIANTS (có mở rộng từng loại)
-- =========================
CREATE TABLE productvariants (
  variantId INT AUTO_INCREMENT PRIMARY KEY,
  productId INT NOT NULL,
  attributes JSON,
  price     DECIMAL(10,2),
  FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Biến thể gốc (từ script trước)
INSERT INTO productvariants (productId, attributes, price) VALUES
(1, JSON_OBJECT('size','1kg'),         45000),
(1, JSON_OBJECT('size','2kg'),         88000),
(2, JSON_OBJECT('size','1kg'),         55000),
(3, JSON_OBJECT('size','1kg'),         25000),
(4, JSON_OBJECT('size','1 quả (2kg)'), 60000);

-- Biến thể mở rộng theo từng sản phẩm
-- SP1: Táo đỏ Fuji → thêm 0.5kg, 3kg
INSERT INTO productvariants (productId, attributes, price) VALUES
(1, JSON_OBJECT('size','0.5kg'), 23000),
(1, JSON_OBJECT('size','3kg'),   129000);

-- SP2: Cam Mỹ Sunkist → thêm 0.5kg, 2kg
INSERT INTO productvariants (productId, attributes, price) VALUES
(2, JSON_OBJECT('size','0.5kg'), 29000),
(2, JSON_OBJECT('size','2kg'),   108000);

-- SP3: Chuối tiêu → thêm 2kg, 3kg
INSERT INTO productvariants (productId, attributes, price) VALUES
(3, JSON_OBJECT('size','2kg'), 48000),
(3, JSON_OBJECT('size','3kg'), 70000);

-- SP4: Dưa hấu không hạt → thêm 1/2 quả, 1 quả to
INSERT INTO productvariants (productId, attributes, price) VALUES
(4, JSON_OBJECT('size','1/2 quả (~1kg)'), 32000),
(4, JSON_OBJECT('size','1 quả to (3kg)'), 85000);

-- =========================
-- 5) INVENTORY (tồn kho) + LOG
-- =========================
CREATE TABLE inventories (
  variantId INT PRIMARY KEY,
  quantity  INT NOT NULL DEFAULT 0,
  reserved  INT NOT NULL DEFAULT 0,
  updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (variantId) REFERENCES productvariants(variantId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE inventory_logs (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  variantId INT NOT NULL,
  changeQty INT NOT NULL,
  reason    VARCHAR(100),
  orderId   INT NULL,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (variantId) REFERENCES productvariants(variantId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed tồn kho mặc định cho TẤT CẢ biến thể (30 đơn vị)
INSERT INTO inventories (variantId, quantity, reserved)
SELECT variantId, 30, 0 FROM productvariants;

-- Ghi log seed tương ứng
INSERT INTO inventory_logs (variantId, changeQty, reason)
SELECT variantId, 30, 'seed' FROM productvariants;

-- (Tuỳ chọn) Procedure điều chỉnh tồn kho + log
DELIMITER //
DROP PROCEDURE IF EXISTS sp_adjust_stock //
CREATE PROCEDURE sp_adjust_stock(IN p_variantId INT, IN p_delta INT, IN p_reason VARCHAR(100), IN p_orderId INT)
BEGIN
  INSERT INTO inventories(variantId, quantity)
  VALUES (p_variantId, IFNULL(p_delta,0))
  ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity);

  INSERT INTO inventory_logs(variantId, changeQty, reason, orderId)
  VALUES (p_variantId, p_delta, p_reason, p_orderId);
END //
DELIMITER ;

-- =========================
-- 6) REVIEWS + VIEW AVG RATING
-- =========================
CREATE TABLE reviews (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  user_id    INT NOT NULL,
  rating     INT CHECK (rating BETWEEN 1 AND 5),
  comment    TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(productId) ON DELETE CASCADE,
  FOREIGN KEY (user_id)   REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO reviews (product_id, user_id, rating, comment) VALUES
(1, 1, 5, 'Táo rất giòn và ngọt, giao hàng nhanh!'),
(1, 2, 4, 'Hàng tươi nhưng hơi ít nước.'),
(2, 1, 5, 'Cam Mỹ thơm ngon, đáng tiền.'),
(3, 2, 3, 'Chuối ổn, nhưng chưa chín kỹ.'),
(4, 1, 5, 'Dưa hấu rất ngon và ngọt.');

CREATE VIEW view_product_avg_rating AS
SELECT 
  p.productId,
  p.name,
  ROUND(AVG(r.rating),1) AS avgRating,
  COUNT(r.id)            AS totalReviews
FROM products p
LEFT JOIN reviews r ON p.productId = r.product_id
GROUP BY p.productId;

-- =========================
-- 7) VIEW TỒN KHO
-- =========================
CREATE VIEW view_variant_stock AS
SELECT v.variantId, v.productId, v.price,
       IFNULL(i.quantity,0) AS quantity,
       IFNULL(i.reserved,0) AS reserved,
       GREATEST(IFNULL(i.quantity,0) - IFNULL(i.reserved,0), 0) AS available
FROM productvariants v
LEFT JOIN inventories i ON i.variantId = v.variantId;

CREATE VIEW view_product_stock AS
SELECT p.productId,
       SUM(GREATEST(IFNULL(i.quantity,0) - IFNULL(i.reserved,0),0)) AS totalAvailable
FROM products p
LEFT JOIN productvariants v ON v.productId = p.productId
LEFT JOIN inventories i ON i.variantId = v.variantId
GROUP BY p.productId;

-- =========================
-- 8) INDEXES (tối ưu truy vấn)
-- =========================
CREATE INDEX idx_productimages_product  ON productimages(productId);
CREATE INDEX idx_productvariants_product ON productvariants(productId);
CREATE INDEX idx_reviews_product        ON reviews(product_id);
CREATE INDEX idx_reviews_user           ON reviews(user_id);

-- =========================
-- ✅ TEST QUICK
-- =========================
-- SELECT * FROM products;
-- SELECT * FROM productimages ORDER BY productId, isMain DESC, imageId;
-- SELECT productId, variantId, JSON_EXTRACT(attributes, '$.size') AS size, price FROM productvariants ORDER BY productId, variantId;
-- SELECT * FROM view_variant_stock ORDER BY productId, variantId;
-- SELECT * FROM view_product_stock ORDER BY productId;
-- SELECT * FROM view_product_avg_rating ORDER BY productId;

-- Ví dụ điều chỉnh tồn kho:
-- CALL sp_adjust_stock(1, -2, 'order #123', 123);
