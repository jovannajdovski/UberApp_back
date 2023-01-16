package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideRequestDTO {
    @Length(min = 1, max = 1)
    @Valid
    private Set<PathDTO> locations;
    private VehicleCategory vehicleType;
    @NotNull
    private boolean babyTransport;
    @NotNull
    private boolean petTransport;


    public RideRequestDTO(Ride ride) {
        this(ride.getPaths().stream().map(PathDTO::new).collect(Collectors.toSet()),ride.getVehicleType().getVehicleCategory(), ride.isBabiesAllowed(),ride.isPetsAllowed());
    }

}
