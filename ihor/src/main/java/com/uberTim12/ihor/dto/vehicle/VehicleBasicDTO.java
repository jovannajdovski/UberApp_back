package com.uberTim12.ihor.dto.vehicle;

import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleBasicDTO {
    private VehicleCategory vehicleType;

    private String model;

    private String licenseNumber;
    public VehicleBasicDTO(Vehicle vehicle)
    {
        this.vehicleType=vehicle.getVehicleType().getVehicleCategory();
        this.model=vehicle.getVehicleModel();
        this.licenseNumber=vehicle.getRegistrationPlate();

    }
}
