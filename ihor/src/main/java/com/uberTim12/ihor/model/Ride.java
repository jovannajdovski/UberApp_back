package com.uberTim12.ihor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double totalPrice;

    @OneToOne
    private Driver driver;

    @ManyToMany
    private Set<Passenger> passengers;

    @ManyToMany
    private Set<Path> paths;

    private LocalDateTime estimatedTime;

    @OneToMany
    private Set<Review> reviews;
    @Enumerated
    private RideStatus rideStatus;

    @OneToOne
    private RideRejection rideRejection;

    private boolean isPanicActivated;

    private boolean babiesAllowed;

    private boolean petsAllowed;

    @ManyToOne
    private VehicleType vehicleType;

}
