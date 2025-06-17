package com.omarkanteh.busbooking.entities;

import com.omarkanteh.busbooking.enums.BusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "buses")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "bus_number")
    private String busNumber;


    @Column(name = "bus_name")
    private String busName;

    @Column(name = "total_seats")
    private int totalSeats;

    @Column(name = "bus_type")
    @Enumerated(EnumType.STRING)
    private BusType busType; // Super Express, Express, Regular


    @OneToMany(mappedBy = "bus")
    private List<BusSchedule> schedules;



}