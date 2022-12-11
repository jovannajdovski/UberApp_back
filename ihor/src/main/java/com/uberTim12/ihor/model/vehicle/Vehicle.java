package com.uberTim12.ihor.model.vehicle;

import com.uberTim12.ihor.model.communication.Review;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.Driver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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

    @Column(name = "vehicle_model", nullable = false)
    private String vehicleModel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicleType_id")
    private VehicleType vehicleType;  // da li enum?

    @Column(name = "registration_plate", nullable = false) //unique=true
    private String registrationPlate;

    @Column(name = "seats", nullable = false)
    private Integer seats;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Location currentLocation;

    @Column(name = "babies_allowed", nullable = false)
    private boolean babiesAllowed;

    @Column(name = "pets_allowed", nullable = false)
    private boolean petsAllowed;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();

    public Vehicle(String vehicleModel, VehicleType vehicleType, String registrationPlate,
                   Integer seats, Location currentLocation, boolean babiesAllowed,
                   boolean petsAllowed) {
        this.vehicleModel = vehicleModel;
        this.vehicleType = vehicleType;
        this.registrationPlate = registrationPlate;
        this.seats = seats;
        this.currentLocation = currentLocation;
        this.babiesAllowed = babiesAllowed;
        this.petsAllowed = petsAllowed;
    }
}
