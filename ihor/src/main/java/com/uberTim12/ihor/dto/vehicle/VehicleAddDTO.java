package com.uberTim12.ihor.dto.vehicle;

import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VehicleAddDTO {
    private VehicleCategory vehicleType;
    private String model;
    private String licenseNumber;
    private LocationDTO currentLocation;
    private Integer passengerSeats;
    private boolean babyTransport;
    private boolean petTransport;

    public VehicleAddDTO(Vehicle vehicle)
    {
        this(vehicle.getVehicleType().getVehicleCategory(),
                vehicle.getVehicleModel(),
                vehicle.getRegistrationPlate(),
                new LocationDTO(vehicle.getCurrentLocation()),
                vehicle.getSeats(),
                vehicle.isBabiesAllowed(),
                vehicle.isPetsAllowed()
        );
    }
}
