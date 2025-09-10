DROP TABLE IF EXISTS `bookings`;
CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    train_id INT,
    from_station_code VARCHAR(10),
    to_station_code VARCHAR(10),
    booking_date DATE,
    travel_date DATE,
    pnr_number VARCHAR(10) UNIQUE,
    total_passengers INT,
    status VARCHAR(20) DEFAULT 'CONFIRMED'
);
truncate table passengers;
ALTER TABLE bookings ADD COLUMN ticket_fare DATE;
ALTER TABLE bookings
ADD fare DECIMAL(10,2);
