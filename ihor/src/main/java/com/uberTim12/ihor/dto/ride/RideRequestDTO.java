package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RideRequestDTO {
    private Set<Path> path;
    private boolean babiesAllowed;

    private boolean petsAllowed;

    private VehicleType vehicleType;

    public RideRequestDTO(Ride ride) {
        this(ride.getPaths(),ride.isBabiesAllowed(),ride.isPetsAllowed(),ride.getVehicleType());
    }

}
