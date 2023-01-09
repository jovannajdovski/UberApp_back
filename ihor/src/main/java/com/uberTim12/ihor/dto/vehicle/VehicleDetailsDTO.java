package com.uberTim12.ihor.dto.vehicle;

import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VehicleDetailsDTO {
    private Integer id;
    private Integer driverId;
    private VehicleCategory vehicleType;
    private String model;
    private String licenseNumber;
    private LocationDTO currentLocation;
    private Integer passengerSeats;
    private boolean babyTransport;
    private boolean petTransport;

    public VehicleDetailsDTO(Vehicle vehicle)
    {
        this(vehicle.getId(),
                vehicle.getDriver().getId(),
                vehicle.getVehicleType().getVehicleCategory(),
                vehicle.getVehicleModel(),
                vehicle.getRegistrationPlate(),
                new LocationDTO(vehicle.getCurrentLocation()),
                vehicle.getSeats(),
                vehicle.isBabiesAllowed(),
                vehicle.isPetsAllowed()
        );
    }
}
