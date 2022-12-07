package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RideRequestDTO {
    private Set<PathDTO> locations;
    private VehicleType vehicleType;
    private boolean babyTransport;
    private boolean petTransport;


    public RideRequestDTO(Ride ride) {
        this(ride.getPaths().stream().map(PathDTO::new).collect(Collectors.toSet()),ride.getVehicleType(), ride.isBabiesAllowed(),ride.isPetsAllowed());
    }

}
