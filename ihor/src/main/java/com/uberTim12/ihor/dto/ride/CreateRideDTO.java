package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateRideDTO {
    @Length(min = 1, max = 1)
    @Valid
    private Set<PathDTO> locations = new HashSet<>();
    @Length(min = 1)
    @Valid
    private Set<UserRideDTO> passengers = new HashSet<>();
    private VehicleCategory vehicleType;
    @NotNull
    private boolean babyTransport;
    @NotNull
    private boolean petTransport;
    @FutureOrPresent //custom
    private LocalDateTime scheduledTime;


    public CreateRideDTO(Ride ride){
        this(ride.getStartTime(),ride.getVehicleType().getVehicleCategory(), ride.isBabiesAllowed(), ride.isPetsAllowed());

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

    public CreateRideDTO(LocalDateTime startTime, VehicleCategory vehicleCategory, boolean babiesAllowed, boolean petsAllowed) {
        this.vehicleType = vehicleCategory;
        this.babyTransport = babiesAllowed;
        this.petTransport = petsAllowed;
        this.scheduledTime =startTime;
    }
}
