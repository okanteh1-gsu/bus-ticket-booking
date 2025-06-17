
CREATE TABLE seat (
    id BIGINT NOT NULL AUTO_INCREMENT,
    seat_number VARCHAR(10) NOT NULL,
    is_booked BOOLEAN NOT NULL,
    schedule_id BIGINT NOT NULL,
    booking_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (schedule_id) REFERENCES bus_schedules(id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);