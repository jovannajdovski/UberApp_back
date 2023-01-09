package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideRequestDTO {
    private Set<PathDTO> locations;
    private VehicleCategory vehicleType;
    private boolean babyTransport;
    private boolean petTransport;


    public RideRequestDTO(Ride ride) {
        this(ride.getPaths().stream().map(PathDTO::new).collect(Collectors.toSet()),ride.getVehicleType().getVehicleCategory(), ride.isBabiesAllowed(),ride.isPetsAllowed());
    }

}
