package com.uberTim12.ihor.model.ride;

import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.model.communication.Review;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class RideReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(
            name="passenger_ride_reservation",
            joinColumns = @JoinColumn(name = "ride_reservation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id")
    )
    private Set<Passenger> passengers = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(
            name="ride_path_reservation",
            joinColumns = @JoinColumn(name = "ride_reservation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "path_id", referencedColumnName = "id")
    )
    private Set<Path> paths = new HashSet<>();

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "estimated_time", nullable = false)
    private Double estimatedTime;

    @Column(name = "babies_allowed", nullable = false)
    private boolean babiesAllowed;

    @Column(name = "pets_allowed", nullable = false)
    private boolean petsAllowed;

    @Column(name = "vehicle_category")
    private VehicleCategory vehicleCategory;

    public RideReservation(CreateRideDTO rideDTO) {
        this.startTime = rideDTO.getStartTime();
        this.babiesAllowed = rideDTO.isBabyTransport();
        this.petsAllowed = rideDTO.isPetTransport();
        this.vehicleCategory = rideDTO.getVehicleCategory();
    }
}
