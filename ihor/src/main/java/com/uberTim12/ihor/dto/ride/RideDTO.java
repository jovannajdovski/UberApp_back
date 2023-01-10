package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRejection;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideDTO {
    private Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalPrice;
    private Driver driver;
    private Set<Passenger> passengers = new HashSet<>();
    private Double estimatedTime;
    private VehicleType vehicleType;
    private boolean babiesAllowed;
    private boolean petsAllowed;
    private RideRejection rejection;
    private Set<Path> locations = new HashSet<>();

    public RideDTO(Ride ride)
    {
        this(ride.getId(),
                ride.getStartTime(),
                ride.getEndTime(),
                ride.getTotalPrice(),
                ride.getDriver(),
                ride.getPassengers(),
                ride.getEstimatedTime(),
                ride.getVehicleType(),
                ride.isBabiesAllowed(),
                ride.isPetsAllowed(),
                ride.getRideRejection(),
                ride.getPaths());
    }


}
