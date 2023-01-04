package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.RideReservation;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RideReservationFullDTO {
    private Integer id;
    private LocalDateTime startTime;

    private Double totalCost;

    private Set<UserRideDTO> passengers = new HashSet<>();

    private Double estimatedTimeInMinutes;

    private VehicleCategory vehicleType;
    private boolean babyTransport;

    private boolean petTransport;

    private Set<PathDTO> locations = new HashSet<>();

    public RideReservationFullDTO(RideReservation rideReservation)
    {
        this.id=rideReservation.getId();
        this.babyTransport=rideReservation.isBabiesAllowed();
        this.petTransport=rideReservation.isPetsAllowed();
        this.startTime=rideReservation.getStartTime();
        this.vehicleType=rideReservation.getVehicleCategory();
        this.totalCost=rideReservation.getTotalPrice();
        this.estimatedTimeInMinutes=rideReservation.getEstimatedTime();

        Set<UserRideDTO> passengers = new HashSet<>();
        for (User u : rideReservation.getPassengers()){
            passengers.add(new UserRideDTO(u));
        }
        this.passengers = passengers;

        Set<PathDTO> locations = new HashSet<>();
        for (Path p : rideReservation.getPaths()){
            locations.add(new PathDTO(p));
        }
        this.locations = locations;

    }
}
