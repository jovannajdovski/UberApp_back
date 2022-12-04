package com.uberTim12.ihor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private Driver driver;

    private String vehicleModel;

    @ManyToOne
    private VehicleType vehicleType;  // da li enum?

    private String registrationPlate;

    private Integer seats;

    @ManyToOne
    private Location currentLocation;

    private boolean babiesAllowed;

    private boolean petsAllowed;

    @OneToMany
    private Set<Review> reviews;
}
