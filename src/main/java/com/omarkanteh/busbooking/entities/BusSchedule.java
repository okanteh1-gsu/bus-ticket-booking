package com.omarkanteh.busbooking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "bus_schedules")
public class BusSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;


    @Column(name = "departure_time", nullable = false)
    @NotNull(message = "Departure time is required.")
    private LocalDateTime departureTime;


    @Column(name = "arrival_time", nullable = false)
    @NotNull(message = "Arrival time is required.")
    @FutureOrPresent(message = "Arrival time must be in the future or present.")
    private LocalDateTime arrivalTime;


    private BigDecimal fare;

    @OneToMany(mappedBy = "schedule", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;

    // Available seats count, constructors, getters/setters
}
