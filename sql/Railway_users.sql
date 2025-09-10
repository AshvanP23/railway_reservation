DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `users` VALUES ('rail','rail','rail@gmail.com','1111111111'),('siva','ssss','siva@gmail.com','6369888800');

SHOW TABLES;
SELECT * FROM users;
SELECT * FROM trains;
SELECT * FROM train_routes;
SELECT * FROM passengers;
SELECT * FROM bookings;

