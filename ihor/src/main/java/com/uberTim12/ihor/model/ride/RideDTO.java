package com.uberTim12.ihor.model.ride;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RideDTO {
    private Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalPrice;
    private Driver driver;
    private Set<Passenger> passengers = new HashSet<>();
    private Double estimatedTime;
    private boolean babiesAllowed;
    private boolean petsAllowed;
    private VehicleType vehicleType;

    public RideDTO(Ride ride)
    {
        this(ride.getId(),
                ride.getStartTime(),
                ride.getEndTime(),
                ride.getTotalPrice(),
                ride.getDriver(),
                ride.getPassengers(),
                ride.getEstimatedTime(),
                ride.isBabiesAllowed(),
                ride.isPetsAllowed(),
                ride.getVehicleType());
    }


}
