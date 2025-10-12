-- promotions.sql
USE bachhoa;

-- Bảng promotions
DROP TABLE IF EXISTS promotions;
CREATE TABLE promotions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(100) NOT NULL UNIQUE,         -- mã giảm giá (vd: SALE20)
  title VARCHAR(255) NULL,                   -- tiêu đề ngắn
  description TEXT NULL,
  discountType ENUM('PERCENTAGE','FIXED') NOT NULL DEFAULT 'PERCENTAGE',
  discountValue DECIMAL(10,2) NOT NULL,      -- nếu PERCENTAGE -> 10 => 10%
  minOrderAmount DECIMAL(12,2) DEFAULT 0.00, -- điều kiện tối thiểu (optional)
  active BOOLEAN DEFAULT TRUE,
  startAt DATETIME NOT NULL,
  endAt DATETIME NOT NULL,
  createdBy INT NULL,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updatedAt TIMESTAMP NULL,
  CONSTRAINT chk_promo_dates CHECK (startAt < endAt)
);

-- Mapping promotion -> category (apply promotion to multiple categories)
DROP TABLE IF EXISTS promotion_categories;
CREATE TABLE promotion_categories (
  promotionId INT NOT NULL,
  categoryId INT NOT NULL,
  PRIMARY KEY (promotionId, categoryId),
  FOREIGN KEY (promotionId) REFERENCES promotions(id) ON DELETE CASCADE,
  -- assumes categories exists as in your data.sql with categoryId
  FOREIGN KEY (categoryId) REFERENCES categories(categoryId) ON DELETE CASCADE
);

-- Optional: map promotion -> products if needed in future
DROP TABLE IF EXISTS promotion_products;
CREATE TABLE promotion_products (
  promotionId INT NOT NULL,
  productId INT NOT NULL,
  PRIMARY KEY (promotionId, productId),
  FOREIGN KEY (promotionId) REFERENCES promotions(id) ON DELETE CASCADE,
  FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE
);

-- Example seed (uncomment to test)
-- INSERT INTO promotions (code, title, description, discountType, discountValue, minOrderAmount, startAt, endAt, createdBy)
-- VALUES ('FALL10','Giảm 10% mùa thu','Giảm 10% cho nhóm trái cây', 'PERCENTAGE', 10.00, 0.00, '2025-10-01 00:00:00','2025-10-10 23:59:59', 2);
-- INSERT INTO promotion_categories (promotionId, categoryId) VALUES (1, 1);
