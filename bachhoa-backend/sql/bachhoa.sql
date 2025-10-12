CREATE DATABASE  IF NOT EXISTS `bachhoa` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bachhoa`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+07:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `phoneNumber` varchar(10) NOT NULL,
  `passwordHash` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `role` varchar(20) NOT NULL DEFAULT 'customer',
  PRIMARY KEY (`userId`),
  UNIQUE KEY `UK_username` (`username`),
  UNIQUE KEY `UK_phoneNumber` (`phoneNumber`),
  UNIQUE KEY `UK_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--
LOCK TABLES `users` WRITE;
INSERT INTO `users` (`userId`, `username`, `phoneNumber`, `passwordHash`, `email`, `createdAt`, `role`)
VALUES 
(1,'testuser','0123456789','$2a$10$E.M/fQWWoo6aBo1y4Uq1/uWpA.20j.y.jW.B7Q.J.x.X.Y.Z.a.b.c','testuser@example.com','2025-10-12 20:00:00','customer'),
(2,'adminuser','0987654321','$2a$10$E.M/fQWWoo6aBo1y4Uq1/uWpA.20j.y.jW.B7Q.J.x.X.Y.Z.a.b.c','admin@example.com','2025-10-12 20:00:00','admin');
UNLOCK TABLES;

-- password 123456
--
-- Table structure for table `user_addresses`
--
DROP TABLE IF EXISTS `user_addresses`;
CREATE TABLE `user_addresses` (
  `addressId` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `label` varchar(100) DEFAULT NULL,
  `addressLine` varchar(200) DEFAULT NULL,
  `city` varchar(80) DEFAULT NULL,
  `country` varchar(80) DEFAULT NULL,
  `postalCode` varchar(20) DEFAULT NULL,
  `isDefault` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`addressId`),
  KEY `FK_user_addresses_user_id` (`user_id`),
  CONSTRAINT `FK_user_addresses_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `suppliers`
--
DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE `suppliers` (
  `supplierId` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `contactName` varchar(100) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`supplierId`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `suppliers` WRITE;
INSERT INTO `suppliers` VALUES (1,'CP Foods','Nguyễn Văn A','0901234567'),(2,'Dalat Farm','Trần Thị B','0987654321'),(3,'Vissan','Lê Văn C','0123456789'),(4,'Acecook Việt Nam','Nguyễn Thị Lan','0905566778'),(5,'Tường An','Lê Văn Bình','0903344556'),(6,'Nam Ngư','Trần Hồng Nhung','0911222333'),(7,'Hải Châu Food','Phạm Quang Huy','0977555444'),(8,'Milo Nestlé','Ngô Thị Dung','0933222111'),(9,'CP Frozen Food','Đỗ Thành Tâm','0909112233'),(10,'Oishi Việt Nam','Vũ Minh Châu','0988997766');
UNLOCK TABLES;

--
-- Table structure for table `categories`
--
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `categoryId` int NOT NULL AUTO_INCREMENT,
  `parentId` int DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`categoryId`),
  KEY `FK_categories_parentId` (`parentId`),
  CONSTRAINT `FK_categories_parentId` FOREIGN KEY (`parentId`) REFERENCES `categories` (`categoryId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `categories` WRITE;
INSERT INTO `categories` VALUES (1,NULL,'Thực phẩm tươi sống','Các loại thịt, cá, hải sản tươi.'),(2,NULL,'Rau củ','Các loại rau, củ, quả sạch.'),(3,NULL,'Đồ đông lạnh','Thực phẩm được bảo quản đông lạnh.'),(4,NULL,'Gia vị','Các loại gia vị nấu ăn: muối, tiêu, đường, nước mắm.'),(5,NULL,'Thực phẩm đóng gói sẵn','Các loại đồ hộp, mì gói, xúc xích, snack.');
UNLOCK TABLES;

--
-- Table structure for table `products`
--
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `productId` int NOT NULL AUTO_INCREMENT,
  `sku` varchar(50) DEFAULT NULL,
  `name` varchar(150) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `basePrice` decimal(10,2) DEFAULT NULL,
  `categoryId` int DEFAULT NULL,
  `supplierId` int DEFAULT NULL,
  PRIMARY KEY (`productId`),
  UNIQUE KEY `uk_products_sku` (`sku`),
  KEY `idx_products_category` (`categoryId`),
  KEY `idx_products_sku` (`sku`),
  KEY `FK_products_supplierId` (`supplierId`),
  CONSTRAINT `FK_products_categoryId` FOREIGN KEY (`categoryId`) REFERENCES `categories` (`categoryId`),
  CONSTRAINT `FK_products_supplierId` FOREIGN KEY (`supplierId`) REFERENCES `suppliers` (`supplierId`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `products` WRITE;
INSERT INTO `products` VALUES (1,'BEEF-001','Ba chỉ bò Mỹ Black Angus','Thịt ba chỉ bò Mỹ Black Angus, thích hợp cho món nướng và lẩu.',250000.00,1,1),(2,'PORK-001','Sườn non heo Vissan','Sườn non heo tươi ngon, đã được cắt sẵn.',120000.00,1,3),(3,'CHICK-001','Ức gà phi lê CP Frozen','Thịt ức gà phi lê, ít béo, bảo quản lạnh.',95000.00,1,9),(4,'FISH-001','Cá basa fillet','Cá basa fillet tươi, không xương, dễ chế biến.',75000.00,1,1),(5,'PRAWN-001','Tôm sú Đà Lạt Farm','Tôm sú loại 1, tươi sống, bảo quản lạnh trong ngày.',185000.00,1,2),(6,'EGG-001','Trứng gà ta sạch 10 quả','Trứng gà ta thả vườn, giàu dinh dưỡng.',38000.00,1,1),(7,'VEGE-001','Cà chua Đà Lạt','Cà chua hữu cơ trồng tại nông trại Đà Lạt.',30000.00,2,2),(8,'VEGE-002','Rau cải xanh VietGAP','Rau cải xanh tươi, trồng theo tiêu chuẩn VietGAP.',25000.00,2,2),(9,'VEGE-003','Khoai tây vàng Úc','Khoai tây vàng nhập khẩu từ Úc, dẻo và thơm.',35000.00,2,2),(10,'VEGE-004','Cà rốt Đà Lạt','Cà rốt tươi, giòn, ngọt tự nhiên.',27000.00,2,2),(11,'VEGE-005','Dưa leo hữu cơ','Dưa leo không thuốc trừ sâu, an toàn.',29000.00,2,2),(12,'VEGE-006','Rau muống đồng xanh','Rau muống non, xanh mướt, tươi giòn.',23000.00,2,2),(13,'FROZEN-001','Tôm bóc vỏ đông lạnh 500g','Tôm bóc vỏ làm sạch, cấp đông nhanh, giữ nguyên vị ngọt.',165000.00,3,9),(14,'FROZEN-002','Há cảo tôm thịt 400g','Há cảo nhân tôm thịt, hấp hoặc chiên đều ngon.',98000.00,3,9),(15,'FROZEN-003','Cá hồi cắt lát 200g','Cá hồi Nauy cắt lát, bảo quản cấp đông sâu.',198000.00,3,9),(16,'FROZEN-004','Xúc xích Đức đông lạnh 500g','Xúc xích heo xông khói nhập khẩu từ Đức.',115000.00,3,1),(17,'FROZEN-005','Bánh bao kim sa 6 cái','Bánh bao nhân trứng muối, cấp đông, hấp nóng là dùng được.',85000.00,3,9),(18,'FROZEN-006','Chả giò tôm cua đông lạnh','Chả giò hải sản chiên sẵn, tiện lợi khi dùng.',99000.00,3,1),(19,'SPICE-001','Nước mắm Nam Ngư 40° đạm','Nước mắm truyền thống, vị đậm đà, thơm ngon.',45000.00,4,6),(20,'SPICE-002','Dầu ăn Tường An 1L','Dầu ăn tinh luyện, thích hợp cho chiên xào.',48000.00,4,5),(21,'SPICE-003','Bột canh Hải Châu 200g','Gia vị nêm sẵn, tiện lợi cho mọi món ăn.',22000.00,4,7),(22,'SPICE-004','Muối hồng Himalaya 500g','Muối khoáng thiên nhiên, không tạp chất.',60000.00,4,6),(23,'SPICE-005','Hạt tiêu đen Phú Quốc 100g','Hạt tiêu nguyên chất, thơm cay tự nhiên.',55000.00,4,6),(24,'SPICE-006','Đường trắng Biên Hòa 1kg','Đường tinh luyện, ngọt thanh, dễ hòa tan.',32000.00,4,7),(25,'PACK-001','Mì Omachi ly','Mì ăn liền Omachi vị xốt bò hầm.',12000.00,5,4),(26,'PACK-002','Snack Oishi 50g','Snack khoai tây giòn ngon, vị phô mai.',15000.00,5,10),(27,'PACK-003','Ngũ cốc Milo 240ml','Thức uống ngũ cốc cung cấp năng lượng buổi sáng.',18000.00,5,8),(28,'PACK-004','Phở Đệ Nhất 70g','Phở ăn liền Đệ Nhất, vị bò đặc trưng.',13000.00,5,4),(29,'PACK-005','Bánh quy AFC hộp 200g','Bánh quy dinh dưỡng vị lúa mì và phô mai.',42000.00,5,7),(30,'PACK-006','Xúc xích CP 200g','Xúc xích heo CP đóng gói hút chân không.',48000.00,5,4),(31,'PORK-002','Cốt lết heo Vissan','Thịt cốt lết heo tươi ngon, thích hợp để chiên hoặc nướng.',110000.00,1,3),(32,'BEEF-002','Thăn bò Mỹ Black Angus','Thăn bò mềm, dùng cho steak hoặc nướng.',320000.00,1,1),(33,'CHICK-002','Đùi gà tươi CP','Đùi gà tươi, thịt chắc, phù hợp chiên nướng.',78000.00,1,1),(34,'CHICK-003','Cánh gà tươi CP','Cánh gà tươi, giòn da, dễ chế biến.',89000.00,1,1),(35,'VEGE-007','Ớt chuông Đà Lạt','Ớt chuông nhiều màu, tươi giòn, giàu vitamin C.',55000.00,2,2),(36,'VEGE-008','Hành tây vàng Đà Lạt','Hành tây vàng ngọt dịu, dùng cho các món xào và súp.',34000.00,2,2),(37,'SPICE-007','Muối biển Bạc Liêu','Muối biển tự nhiên, hạt mịn, không chất tẩy trắng.',25000.00,4,6),(38,'SPICE-008','Nước tương đậm đặc Maggi','Nước tương lên men tự nhiên, vị mặn dịu.',30000.00,4,7),(39,'FROZEN-007','Chả cá thác lác 400g','Chả cá thác lác tươi, cấp đông nhanh.',99000.00,3,9),(40,'FROZEN-008','Cá viên chiên đông lạnh 500g','Cá viên chiên sẵn, chỉ cần chiên lại là dùng.',85000.00,3,9),(41,'PACK-007','Mì Hảo Hảo tôm chua cay','Mì ăn liền vị tôm chua cay quen thuộc.',9000.00,5,4),(42,'PACK-008','Snack Oishi rong biển 40g','Snack rong biển giòn tan, vị mặn nhẹ.',13000.00,5,10);
UNLOCK TABLES;

--
-- Table structure for table `productvariants`
--
DROP TABLE IF EXISTS `productvariants`;
CREATE TABLE `productvariants` (
  `variantId` int NOT NULL AUTO_INCREMENT,
  `productId` int NOT NULL,
  `variantSku` varchar(50) NOT NULL,
  `attributes` text,
  `price` decimal(10,2) NOT NULL,
  `stockQuantity` int DEFAULT '0',
  PRIMARY KEY (`variantId`),
  KEY `FK_productvariants_productId` (`productId`),
  CONSTRAINT `FK_productvariants_productId` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`)
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `productvariants` WRITE;
INSERT INTO `productvariants` VALUES (101,1,'BEEF-001-SM','{\"size\":\"500g\"}',250000.00,25),(102,1,'BEEF-001-LG','{\"size\":\"1kg\"}',450000.00,12),(103,2,'PORK-001-SM','{\"size\":\"500g\"}',120000.00,35),(104,2,'PORK-001-LG','{\"size\":\"1kg\"}',216000.00,20),(105,3,'CHICK-001-SM','{\"size\":\"500g\"}',95000.00,40),(106,3,'CHICK-001-LG','{\"size\":\"1kg\"}',171000.00,18),(107,4,'FISH-001-SM','{\"size\":\"1kg\"}',75000.00,50),(108,4,'FISH-001-LG','{\"size\":\"2kg\"}',135000.00,20);
UNLOCK TABLES;


-- =========================
-- TABLE: promotions + promotion_categories + promotion_products + promotion_variants
-- =========================

DROP TABLE IF EXISTS `promotion_variants`;
DROP TABLE IF EXISTS `promotion_products`;
DROP TABLE IF EXISTS `promotion_categories`;
DROP TABLE IF EXISTS `promotions`;

-- =========================
-- BẢNG CHÍNH: promotions
-- =========================
CREATE TABLE `promotions` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(50) NOT NULL UNIQUE,
  `title` VARCHAR(255) DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `discountType` ENUM('PERCENT', 'AMOUNT') NOT NULL DEFAULT 'PERCENT',
  `discountValue` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `minOrderAmount` DECIMAL(10,2) DEFAULT NULL,
  `active` TINYINT(1) NOT NULL DEFAULT 1,
  `startAt` DATETIME DEFAULT NULL,
  `endAt` DATETIME DEFAULT NULL,
  `createdBy` INT DEFAULT NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_promotions_code` (`code`),
  CONSTRAINT `fk_promotions_createdBy`
    FOREIGN KEY (`createdBy`) REFERENCES `users` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================
-- BẢNG LIÊN KẾT: promotion_categories
-- =====================================
CREATE TABLE `promotion_categories` (
  `promotionId` INT NOT NULL,
  `categoryId` INT NOT NULL,
  PRIMARY KEY (`promotionId`, `categoryId`),
  KEY `idx_promo_cat_category` (`categoryId`),
  CONSTRAINT `fk_promo_cat_promotion`
    FOREIGN KEY (`promotionId`) REFERENCES `promotions` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_promo_cat_category`
    FOREIGN KEY (`categoryId`) REFERENCES `categories` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================
-- BẢNG LIÊN KẾT: promotion_products
-- =====================================
CREATE TABLE `promotion_products` (
  `promotionId` INT NOT NULL,
  `productId` INT NOT NULL,
  PRIMARY KEY (`promotionId`, `productId`),
  KEY `idx_promo_prod_product` (`productId`),
  CONSTRAINT `fk_promo_prod_promotion`
    FOREIGN KEY (`promotionId`) REFERENCES `promotions` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_promo_prod_product`
    FOREIGN KEY (`productId`) REFERENCES `products` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================
-- BẢNG LIÊN KẾT: promotion_variants
-- =====================================
CREATE TABLE `promotion_variants` (
  `promotionId` INT NOT NULL,
  `variantId` INT NOT NULL,
  PRIMARY KEY (`promotionId`, `variantId`),
  KEY `idx_promo_variant_variant` (`variantId`),
  CONSTRAINT `fk_promo_variant_promotion`
    FOREIGN KEY (`promotionId`) REFERENCES `promotions` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_promo_variant_variant`
    FOREIGN KEY (`variantId`) REFERENCES `productvariants` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================
-- DỮ LIỆU MẪU
-- =====================================
INSERT INTO `promotions`
(`code`, `title`, `description`, `discountType`, `discountValue`, `minOrderAmount`, `active`, `startAt`, `endAt`, `createdBy`)
VALUES
('SUMMER10', 'Summer Sale 10%', 'Giảm 10% nhiều sản phẩm', 'PERCENT', 10.00, 0.00, 1, '2025-06-01 00:00:00', '2025-07-31 23:59:59', 1),
('FRESH50', 'FRESH50 - 50k off', 'Giảm 50000 VND cho đơn hàng từ 200k', 'AMOUNT', 50000.00, 200000.00, 1, '2025-10-01 00:00:00', '2025-10-31 23:59:59', 1),
('VIPVAR', 'VIP Variant Deal', 'Giảm 15% cho một số variant cao cấp', 'PERCENT', 15.00, 0.00, 1, '2025-08-01 00:00:00', '2025-12-31 23:59:59', 2);

-- Mapping mẫu: áp dụng cho category, product và variant
INSERT INTO `promotion_categories` (`promotionId`, `categoryId`) VALUES (1, 1), (2, 2);
INSERT INTO `promotion_products` (`promotionId`, `productId`) VALUES (1, 3), (2, 5);
INSERT INTO `promotion_variants` (`promotionId`, `variantId`) VALUES (3, 7), (3, 8);

--
-- Table structure for table `productimages`
--
DROP TABLE IF EXISTS `productimages`;
CREATE TABLE `productimages` (
  `imageId` int NOT NULL AUTO_INCREMENT,
  `productId` int DEFAULT NULL,
  `variantId` int DEFAULT NULL,
  `imageUrl` varchar(255) DEFAULT NULL,
  `isMain` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`imageId`),
  KEY `FK_productimages_productId` (`productId`),
  KEY `FK_productimages_variantId` (`variantId`),
  CONSTRAINT `FK_productimages_productId` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`),
  CONSTRAINT `FK_productimages_variantId` FOREIGN KEY (`variantId`) REFERENCES `productvariants` (`variantId`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `productimages` WRITE;
INSERT INTO `productimages` VALUES (1,1,NULL,'images/bachibo.jpg',1),(2,2,NULL,'images/suonnonheoVissan.jpg',1),(3,3,NULL,'images/ucga.jpg',1),(4,4,NULL,'images/fish_01.jpg',1),(5,5,NULL,'images/prawn_01.jpg',1),(6,6,NULL,'images/egg_01.jpg',1),(7,7,NULL,'images/tomato_01.jpg',1),(8,8,NULL,'images/caixanh_01.jpg',1),(9,9,NULL,'images/potato_01.jpg',1),(10,10,NULL,'images/carrot_01.jpg',1),(11,11,NULL,'images/dualeo_01.jpg',1),(12,12,NULL,'images/raumuong_01.jpg',1),(13,13,NULL,'images/tomdonglanh_01.jpg',1),(14,14,NULL,'images/hacaotomthit_01.jpg',1),(15,15,NULL,'images/cahoi_01.jpg',1),(16,16,NULL,'images/xucxichDuc_01.jpg',1),(17,17,NULL,'images/banhbaokimsa_01.jpg',1),(18,18,NULL,'images/chagiotomcua_01.png',1),(19,19,NULL,'images/nuocmamNamNgu.jpg',1),(20,20,NULL,'images/dauanTuongAn.jpg',1),(21,21,NULL,'images/botcanhHaiChau.jpg',1),(22,22,NULL,'images/muoihongHimalaya.jpg',1),(23,23,NULL,'images/tieudenPhuQuoc.jpg',1),(24,24,NULL,'images/duongtrangBienHoa.jpg',1),(25,25,NULL,'images/milyOmachi.jpg',1),(26,26,NULL,'images/snackOshi.png',1),(27,27,NULL,'images/ngucocMilo.jpg',1),(28,28,NULL,'images/phoDeNhat.jpg',1),(29,29,NULL,'images/banhquyAFC.jpg',1),(30,30,NULL,'images/xucxichCP.jpg',1),(31,31,NULL,'images/cotletheoVissan.jpg',1),(32,32,NULL,'images/thanboMy.jpg',1),(33,33,NULL,'images/duigaCP.jpg',1),(34,34,NULL,'images/canhgaCP.jpg',1),(35,35,NULL,'images/otchương_01.jpg',1),(36,36,NULL,'images/hanhtay_01.jpg',1),(37,37,NULL,'images/muoibienNhatrang.jpg',1),(38,38,NULL,'images/nuoctuongHaichau.jpg',1),(39,39,NULL,'images/chacathaclac.jpg',1),(40,40,NULL,'images/cavienchien.jpg',1),(41,41,NULL,'images/mihaohao.jpg',1),(42,42,NULL,'images/snackrongbien.jpg',1);
UNLOCK TABLES;

--
-- Table structure for table `review`
--
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `reviewID` int NOT NULL AUTO_INCREMENT,
  `rating` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `comment` text,
  `isApproved` tinyint(1) NOT NULL DEFAULT '0',
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`reviewID`),
  KEY `FK_review_user_id` (`user_id`),
  KEY `FK_review_product_id` (`product_id`),
  CONSTRAINT `FK_review_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `FK_review_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`productId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `review`
--
LOCK TABLES `review` WRITE;
INSERT INTO `review` VALUES (1,5,'Sản phẩm tuyệt vời!','Thịt bò rất tươi và mềm, hợp để ăn lẩu.',1,'2025-10-12 21:00:00',1,1),(2,4,'Chất lượng tốt','Rau củ tươi, giao hàng nhanh.',1,'2025-10-11 15:30:00',1,7);
UNLOCK TABLES;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;