package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.comunication.Review;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRejection;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.User;
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
public class CreateRideDTO {

    private Set<PathDTO> locations = new HashSet<>();

    private Set<UserRideDTO> passengers = new HashSet<>();

    private VehicleCategory vehicleType;
    private boolean babyTransport;

    private boolean petTransport;


    public CreateRideDTO(Ride ride){
        this(ride.getVehicleType().getVehicleCategory(), ride.isBabiesAllowed(), ride.isPetsAllowed());

        Set<UserRideDTO> passengers = new HashSet<>();
        for (User u : ride.getPassengers()){
            passengers.add(new UserRideDTO(u));
        }
        this.passengers = passengers;

        Set<PathDTO> locations = new HashSet<>();
        for (Path p : ride.getPaths()){
            locations.add(new PathDTO(p));
        }
        this.locations = locations;
    }

    public CreateRideDTO(VehicleCategory vehicleCategory, boolean babiesAllowed, boolean petsAllowed) {
        this.vehicleType = vehicleCategory;
        this.babyTransport = babiesAllowed;
        this.petTransport = petsAllowed;
    }
}
