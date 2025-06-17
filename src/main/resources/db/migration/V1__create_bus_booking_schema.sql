-- V1__create_bus_booking_schema.sql

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)
);

-- Buses table
CREATE TABLE buses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_number VARCHAR(50) NOT NULL,
    bus_name VARCHAR(255),
    total_seats INT,
    bus_type ENUM('SUPER_EXPRESS', 'EXPRESS', 'REGULAR')
);

-- Routes table
CREATE TABLE routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    distance INT,
    estimated_duration INT
);

-- BusSchedules table
CREATE TABLE bus_schedules (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               route_id BIGINT NOT NULL,
                               bus_id BIGINT NOT NULL,
                               departure_time DATETIME NOT NULL,
                               arrival_time DATETIME,
                               FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE,
                               FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE
);

-- Bookings table
CREATE TABLE bookings (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          bus_schedule_id BIGINT NOT NULL,
                          booking_time DATETIME NOT NULL,
                          status VARCHAR(20), -- store BookingStatus as string
                          total_fare DOUBLE,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          FOREIGN KEY (bus_schedule_id) REFERENCES bus_schedules(id) ON DELETE CASCADE
);
