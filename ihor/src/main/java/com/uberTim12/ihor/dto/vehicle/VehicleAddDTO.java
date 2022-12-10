package com.uberTim12.ihor.dto.vehicle;

import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VehicleAddDTO {
    private VehicleCategory vehicleType;
    private String model;
    private String licenseNumber;
    private Location currentLocation;
    private Integer passengerSeats;
    private boolean babyTransport;
    private boolean petTransport;

    public VehicleAddDTO(Vehicle vehicle)
    {
        this(vehicle.getVehicleType().getVehicleCategory(),
                vehicle.getVehicleModel(),
                vehicle.getRegistrationPlate(),
                vehicle.getCurrentLocation(),
                vehicle.getSeats(),
                vehicle.isBabiesAllowed(),
                vehicle.isPetsAllowed()
        );
    }
}
