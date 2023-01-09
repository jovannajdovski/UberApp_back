package com.uberTim12.ihor.model.ride;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uberTim12.ihor.model.communication.Review;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(
            name="passenger_ride",
            joinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id")
    )
    private Set<Passenger> passengers = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(
            name="ride_path",
            joinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "path_id", referencedColumnName = "id")
    )
    private Set<Path> paths = new HashSet<>();

    @Column(name = "estimated_time", nullable = false)
    private Double estimatedTime;

    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
    @Enumerated
    @Column(name = "ride_status", nullable = false)
    private RideStatus rideStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private RideRejection rideRejection;

    @Column(name = "is_panic_activated", nullable = false)
    private boolean isPanicActivated;

    @Column(name = "babies_allowed", nullable = false)
    private boolean babiesAllowed;

    @Column(name = "pets_allowed", nullable = false)
    private boolean petsAllowed;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "vehicle_type")
    private VehicleType vehicleType;

}
