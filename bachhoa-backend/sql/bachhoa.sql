CREATE DATABASE  IF NOT EXISTS `bachhoa` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bachhoa`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bachhoa
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `categoryId` int NOT NULL AUTO_INCREMENT,
  `parentId` int DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`categoryId`),
  KEY `FK2r1pcukv08myxrje0r2s55vb0` (`parentId`),
  CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`parentId`) REFERENCES `categories` (`categoryId`),
  CONSTRAINT `FK2r1pcukv08myxrje0r2s55vb0` FOREIGN KEY (`parentId`) REFERENCES `categories` (`categoryId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,NULL,'Thực phẩm tươi sống','Các loại thịt, cá, hải sản tươi.'),(2,NULL,'Rau củ','Các loại rau, củ, quả sạch.'),(3,NULL,'Đồ đông lạnh','Thực phẩm được bảo quản đông lạnh.'),(4,NULL,'Gia vị','Các loại gia vị nấu ăn: muối, tiêu, đường, nước mắm.'),(5,NULL,'Thực phẩm đóng gói sẵn','Các loại đồ hộp, mì gói, xúc xích, snack.');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productimages`
--

DROP TABLE IF EXISTS `productimages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productimages` (
  `imageId` int NOT NULL AUTO_INCREMENT,
  `productId` int DEFAULT NULL,
  `variantId` int DEFAULT NULL,
  `imageUrl` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `isMain` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`imageId`),
  KEY `variantId` (`variantId`),
  KEY `FK4oact84qfy0smlvkq3g1gfvfs` (`productId`),
  CONSTRAINT `FK4oact84qfy0smlvkq3g1gfvfs` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`),
  CONSTRAINT `productimages_ibfk_1` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`),
  CONSTRAINT `productimages_ibfk_2` FOREIGN KEY (`variantId`) REFERENCES `productvariants` (`variantId`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productimages`
--

LOCK TABLES `productimages` WRITE;
/*!40000 ALTER TABLE `productimages` DISABLE KEYS */;
INSERT INTO `productimages` VALUES (1,1,NULL,'images/bachibo.jpg',1),(2,2,NULL,'images/suonnonheoVissan.jpg',1),(3,3,NULL,'images/ucga.jpg',1),(4,4,NULL,'images/fish_01.jpg',1),(5,5,NULL,'images/prawn_01.jpg',1),(6,6,NULL,'images/egg_01.jpg',1),(7,7,NULL,'images/tomato_01.jpg',1),(8,8,NULL,'images/caixanh_01.jpg',1),(9,9,NULL,'images/potato_01.jpg',1),(10,10,NULL,'images/carrot_01.jpg',1),(11,11,NULL,'images/dualeo_01.jpg',1),(12,12,NULL,'images/raumuong_01.jpg',1),(13,13,NULL,'images/tomdonglanh_01.jpg',1),(14,14,NULL,'images/hacaotomthit_01.jpg',1),(15,15,NULL,'images/cahoi_01.jpg',1),(16,16,NULL,'images/xucxichDuc_01.jpg',1),(17,17,NULL,'images/banhbaokimsa_01.jpg',1),(18,18,NULL,'images/chagiotomcua_01.png',1),(19,19,NULL,'images/nuocmamNamNgu.jpg',1),(20,20,NULL,'images/dauanTuongAn.jpg',1),(21,21,NULL,'images/botcanhHaiChau.jpg',1),(22,22,NULL,'images/muoihongHimalaya.jpg',1),(23,23,NULL,'images/tieudenPhuQuoc.jpg',1),(24,24,NULL,'images/duongtrangBienHoa.jpg',1),(25,25,NULL,'images/milyOmachi.jpg',1),(26,26,NULL,'images/snackOshi.png',1),(27,27,NULL,'images/ngucocMilo.jpg',1),(28,28,NULL,'images/phoDeNhat.jpg',1),(29,29,NULL,'images/banhquyAFC.jpg',1),(30,30,NULL,'images/xucxichCP.jpg',1),(31,31,NULL,'images/cotletheoVissan.jpg',1),(32,32,NULL,'images/thanboMy.jpg',1),(33,33,NULL,'images/duigaCP.jpg',1),(34,34,NULL,'images/canhgaCP.jpg',1),(35,35,NULL,'images/otchương_01.jpg',1),(36,36,NULL,'images/hanhtay_01.jpg',1),(37,37,NULL,'images/muoibienNhatrang.jpg',1),(38,38,NULL,'images/nuoctuongHaichau.jpg',1),(39,39,NULL,'images/chacathaclac.jpg',1),(40,40,NULL,'images/cavienchien.jpg',1),(41,41,NULL,'images/mihaohao.jpg',1),(42,42,NULL,'images/snackrongbien.jpg',1);
/*!40000 ALTER TABLE `productimages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `productId` int NOT NULL AUTO_INCREMENT,
  `sku` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `name` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `basePrice` decimal(10,2) DEFAULT NULL,
  `categoryId` int DEFAULT NULL,
  `supplierId` int DEFAULT NULL,
  PRIMARY KEY (`productId`),
  UNIQUE KEY `uk_products_sku` (`sku`),
  KEY `idx_products_category` (`categoryId`),
  KEY `idx_products_sku` (`sku`),
  KEY `FKs2xbxi7wmu948op6qiho9yr8d` (`supplierId`),
  CONSTRAINT `FKej2ob3ifydf846t2a2tntna4e` FOREIGN KEY (`categoryId`) REFERENCES `categories` (`categoryId`),
  CONSTRAINT `FKs2xbxi7wmu948op6qiho9yr8d` FOREIGN KEY (`supplierId`) REFERENCES `suppliers` (`supplierId`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`categoryId`) REFERENCES `categories` (`categoryId`),
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`supplierId`) REFERENCES `suppliers` (`supplierId`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'BEEF-001','Ba chỉ bò Mỹ Black Angus','Thịt ba chỉ bò Mỹ Black Angus, thích hợp cho món nướng và lẩu.',250000.00,1,1),(2,'PORK-001','Sườn non heo Vissan','Sườn non heo tươi ngon, đã được cắt sẵn.',120000.00,1,3),(3,'CHICK-001','Ức gà phi lê CP Frozen','Thịt ức gà phi lê, ít béo, bảo quản lạnh.',95000.00,1,9),(4,'FISH-001','Cá basa fillet','Cá basa fillet tươi, không xương, dễ chế biến.',75000.00,1,1),(5,'PRAWN-001','Tôm sú Đà Lạt Farm','Tôm sú loại 1, tươi sống, bảo quản lạnh trong ngày.',185000.00,1,2),(6,'EGG-001','Trứng gà ta sạch 10 quả','Trứng gà ta thả vườn, giàu dinh dưỡng.',38000.00,1,1),(7,'VEGE-001','Cà chua Đà Lạt','Cà chua hữu cơ trồng tại nông trại Đà Lạt.',30000.00,2,2),(8,'VEGE-002','Rau cải xanh VietGAP','Rau cải xanh tươi, trồng theo tiêu chuẩn VietGAP.',25000.00,2,2),(9,'VEGE-003','Khoai tây vàng Úc','Khoai tây vàng nhập khẩu từ Úc, dẻo và thơm.',35000.00,2,2),(10,'VEGE-004','Cà rốt Đà Lạt','Cà rốt tươi, giòn, ngọt tự nhiên.',27000.00,2,2),(11,'VEGE-005','Dưa leo hữu cơ','Dưa leo không thuốc trừ sâu, an toàn.',29000.00,2,2),(12,'VEGE-006','Rau muống đồng xanh','Rau muống non, xanh mướt, tươi giòn.',23000.00,2,2),(13,'FROZEN-001','Tôm bóc vỏ đông lạnh 500g','Tôm bóc vỏ làm sạch, cấp đông nhanh, giữ nguyên vị ngọt.',165000.00,3,9),(14,'FROZEN-002','Há cảo tôm thịt 400g','Há cảo nhân tôm thịt, hấp hoặc chiên đều ngon.',98000.00,3,9),(15,'FROZEN-003','Cá hồi cắt lát 200g','Cá hồi Nauy cắt lát, bảo quản cấp đông sâu.',198000.00,3,9),(16,'FROZEN-004','Xúc xích Đức đông lạnh 500g','Xúc xích heo xông khói nhập khẩu từ Đức.',115000.00,3,1),(17,'FROZEN-005','Bánh bao kim sa 6 cái','Bánh bao nhân trứng muối, cấp đông, hấp nóng là dùng được.',85000.00,3,9),(18,'FROZEN-006','Chả giò tôm cua đông lạnh','Chả giò hải sản chiên sẵn, tiện lợi khi dùng.',99000.00,3,1),(19,'SPICE-001','Nước mắm Nam Ngư 40° đạm','Nước mắm truyền thống, vị đậm đà, thơm ngon.',45000.00,4,6),(20,'SPICE-002','Dầu ăn Tường An 1L','Dầu ăn tinh luyện, thích hợp cho chiên xào.',48000.00,4,5),(21,'SPICE-003','Bột canh Hải Châu 200g','Gia vị nêm sẵn, tiện lợi cho mọi món ăn.',22000.00,4,7),(22,'SPICE-004','Muối hồng Himalaya 500g','Muối khoáng thiên nhiên, không tạp chất.',60000.00,4,6),(23,'SPICE-005','Hạt tiêu đen Phú Quốc 100g','Hạt tiêu nguyên chất, thơm cay tự nhiên.',55000.00,4,6),(24,'SPICE-006','Đường trắng Biên Hòa 1kg','Đường tinh luyện, ngọt thanh, dễ hòa tan.',32000.00,4,7),(25,'PACK-001','Mì Omachi ly','Mì ăn liền Omachi vị xốt bò hầm.',12000.00,5,4),(26,'PACK-002','Snack Oishi 50g','Snack khoai tây giòn ngon, vị phô mai.',15000.00,5,10),(27,'PACK-003','Ngũ cốc Milo 240ml','Thức uống ngũ cốc cung cấp năng lượng buổi sáng.',18000.00,5,8),(28,'PACK-004','Phở Đệ Nhất 70g','Phở ăn liền Đệ Nhất, vị bò đặc trưng.',13000.00,5,4),(29,'PACK-005','Bánh quy AFC hộp 200g','Bánh quy dinh dưỡng vị lúa mì và phô mai.',42000.00,5,7),(30,'PACK-006','Xúc xích CP 200g','Xúc xích heo CP đóng gói hút chân không.',48000.00,5,4),(31,'PORK-002','Cốt lết heo Vissan','Thịt cốt lết heo tươi ngon, thích hợp để chiên hoặc nướng.',110000.00,1,3),(32,'BEEF-002','Thăn bò Mỹ Black Angus','Thăn bò mềm, dùng cho steak hoặc nướng.',320000.00,1,1),(33,'CHICK-002','Đùi gà tươi CP','Đùi gà tươi, thịt chắc, phù hợp chiên nướng.',78000.00,1,1),(34,'CHICK-003','Cánh gà tươi CP','Cánh gà tươi, giòn da, dễ chế biến.',89000.00,1,1),(35,'VEGE-007','Ớt chuông Đà Lạt','Ớt chuông nhiều màu, tươi giòn, giàu vitamin C.',55000.00,2,2),(36,'VEGE-008','Hành tây vàng Đà Lạt','Hành tây vàng ngọt dịu, dùng cho các món xào và súp.',34000.00,2,2),(37,'SPICE-007','Muối biển Bạc Liêu','Muối biển tự nhiên, hạt mịn, không chất tẩy trắng.',25000.00,4,6),(38,'SPICE-008','Nước tương đậm đặc Maggi','Nước tương lên men tự nhiên, vị mặn dịu.',30000.00,4,7),(39,'FROZEN-007','Chả cá thác lác 400g','Chả cá thác lác tươi, cấp đông nhanh.',99000.00,3,9),(40,'FROZEN-008','Cá viên chiên đông lạnh 500g','Cá viên chiên sẵn, chỉ cần chiên lại là dùng.',85000.00,3,9),(41,'PACK-007','Mì Hảo Hảo tôm chua cay','Mì ăn liền vị tôm chua cay quen thuộc.',9000.00,5,4),(42,'PACK-008','Snack Oishi rong biển 40g','Snack rong biển giòn tan, vị mặn nhẹ.',13000.00,5,10);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productvariants`
--

DROP TABLE IF EXISTS `productvariants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productvariants` (
  `variantId` int NOT NULL AUTO_INCREMENT,
  `productId` int NOT NULL,
  `variantSku` varchar(50) NOT NULL,
  `attributes` text,
  `price` decimal(10,2) NOT NULL,
  `stockQuantity` int DEFAULT '0',
  PRIMARY KEY (`variantId`),
  KEY `FKcvospj50i1y9mhgdq5vlfasbo` (`productId`),
  CONSTRAINT `FKcvospj50i1y9mhgdq5vlfasbo` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`),
  CONSTRAINT `productvariants_ibfk_1` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`)
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productvariants`
--

LOCK TABLES `productvariants` WRITE;
/*!40000 ALTER TABLE `productvariants` DISABLE KEYS */;
INSERT INTO `productvariants` VALUES (101,1,'BEEF-001-SM','500g',250000.00,25),(102,1,'BEEF-001-LG','1kg',450000.00,12),(103,2,'PORK-001-SM','500g',120000.00,35),(104,2,'PORK-001-LG','1kg',216000.00,20),(105,3,'CHICK-001-SM','500g',95000.00,40),(106,3,'CHICK-001-LG','1kg',171000.00,18),(107,4,'FISH-001-SM','1kg',75000.00,50),(108,4,'FISH-001-LG','2kg',135000.00,20),(109,5,'PRAWN-001-SM','500g',185000.00,25),(110,5,'PRAWN-001-LG','1kg',333000.00,15),(111,6,'EGG-001-SM','Hộp 10 trứng',38000.00,60),(112,6,'EGG-001-LG','Vỉ 30 trứng',68400.00,25),(113,7,'VEGE-001-SM','500g',30000.00,40),(114,7,'VEGE-001-LG','1kg',54000.00,20),(115,8,'VEGE-002-SM','500g',25000.00,35),(116,8,'VEGE-002-LG','1kg',45000.00,15),(117,9,'VEGE-003-SM','500g',35000.00,30),(118,9,'VEGE-003-LG','1kg',63000.00,18),(119,10,'VEGE-004-SM','500g',27000.00,50),(120,10,'VEGE-004-LG','1kg',48600.00,20),(121,11,'VEGE-005-SM','500g',29000.00,40),(122,11,'VEGE-005-LG','1kg',52200.00,15),(123,12,'VEGE-006-SM','500g',28000.00,38),(124,12,'VEGE-006-LG','1kg',50400.00,18),(125,13,'FROZEN-001-SM','Gói 200g',165000.00,30),(126,13,'FROZEN-001-LG','Gói 500g',297000.00,15),(127,14,'FROZEN-002-SM','400g',98000.00,40),(128,14,'FROZEN-002-LG','1kg',176000.00,18),(129,15,'FROZEN-003-SM','500g',115000.00,32),(130,15,'FROZEN-003-LG','1kg',207000.00,14),(131,16,'FROZEN-004-SM','6 cái',85000.00,25),(132,16,'FROZEN-004-LG','12 cái',153000.00,12),(133,17,'FROZEN-005-SM','400g',99000.00,30),(134,17,'FROZEN-005-LG','1kg',178000.00,15),(135,18,'FROZEN-006-SM','400g',99000.00,40),(136,18,'FROZEN-006-LG','1kg',178000.00,15),(137,19,'SPICE-001-SM','Chai 500ml',45000.00,60),(138,19,'SPICE-001-LG','Chai 1L',81000.00,25),(139,20,'SPICE-002-SM','500ml',48000.00,50),(140,20,'SPICE-002-LG','1L',86400.00,20),(141,21,'SPICE-003-SM','100g',22000.00,45),(142,21,'SPICE-003-LG','250g',39600.00,20),(143,22,'SPICE-004-SM','250g',62000.00,30),(144,22,'SPICE-004-LG','500g',111600.00,15),(145,23,'SPICE-005-SM','100g',55000.00,30),(146,23,'SPICE-005-LG','250g',99000.00,12),(147,24,'SPICE-006-SM','200g',38000.00,40),(148,24,'SPICE-006-LG','500g',68400.00,20),(149,25,'PACK-001-SM','Gói nhỏ',12000.00,50),(150,25,'PACK-001-LG','Gói lớn',21600.00,25),(151,31,'PORK-002-SM','500g',110000.00,30),(152,31,'PORK-002-LG','1kg',200000.00,15),(153,32,'BEEF-002-SM','300g',150000.00,25),(154,32,'BEEF-002-LG','500g',250000.00,10),(155,33,'CHICK-002-SM','500g',78000.00,35),(156,33,'CHICK-002-LG','1kg',140000.00,20),(157,34,'CHICK-003-SM','500g',89000.00,40),(158,34,'CHICK-003-LG','1kg',160000.00,20),(159,35,'VEGE-007-SM','500g',55000.00,30),(160,35,'VEGE-007-LG','1kg',99000.00,15),(161,36,'VEGE-008-SM','500g',34000.00,40),(162,36,'VEGE-008-LG','1kg',62000.00,18),(163,37,'SPICE-007-SM','250g',25000.00,50),(164,37,'SPICE-007-LG','500g',45000.00,25),(165,38,'SPICE-008-SM','Chai 500ml',30000.00,60),(166,38,'SPICE-008-LG','Chai 1L',54000.00,30),(167,39,'FROZEN-007-SM','400g',99000.00,25),(168,39,'FROZEN-007-LG','1kg',175000.00,12),(169,40,'FROZEN-008-SM','500g',85000.00,30),(170,40,'FROZEN-008-LG','1kg',150000.00,15),(171,41,'PACK-007-SM','Gói nhỏ',9000.00,80),(172,41,'PACK-007-LG','Thùng 30 gói',250000.00,5),(173,42,'PACK-008-SM','Gói 40g',13000.00,50),(174,42,'PACK-008-LG','Túi 80g',24000.00,30);
/*!40000 ALTER TABLE `productvariants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suppliers` (
  `supplierId` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `contactName` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`supplierId`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suppliers`
--

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
INSERT INTO `suppliers` VALUES (1,'CP Foods','Nguyễn Văn A','0901234567'),(2,'Dalat Farm','Trần Thị B','0987654321'),(3,'Vissan','Lê Văn C','0123456789'),(4,'Acecook Việt Nam','Nguyễn Thị Lan','0905566778'),(5,'Tường An','Lê Văn Bình','0903344556'),(6,'Nam Ngư','Trần Hồng Nhung','0911222333'),(7,'Hải Châu Food','Phạm Quang Huy','0977555444'),(8,'Milo Nestlé','Ngô Thị Dung','0933222111'),(9,'CP Frozen Food','Đỗ Thành Tâm','0909112233'),(10,'Oishi Việt Nam','Vũ Minh Châu','0988997766');
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-11 20:30:52
