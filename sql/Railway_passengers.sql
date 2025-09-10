DROP TABLE IF EXISTS `passengers`;
CREATE TABLE `passengers` (
  `passenger_id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`passenger_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
ALTER TABLE passengers ADD COLUMN passenger_type VARCHAR(20);
ALTER TABLE passengers
DROP COLUMN passenger_type;