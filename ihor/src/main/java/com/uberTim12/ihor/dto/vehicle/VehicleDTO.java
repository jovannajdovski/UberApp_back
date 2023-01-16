package com.uberTim12.ihor.dto.vehicle;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VehicleDTO {
    @Min(value = 1)
    private Integer id;
    @NotNull
    private VehicleCategory vehicleType;
    @NotEmpty
    private String model;
    @NotEmpty
    private String licenseNumber;
    @Valid
    private LocationDTO currentLocation;
    @Min(value = 1)
    private Integer passengerSeats;
    @NotNull
    private boolean babyTransport;
    @NotNull
    private boolean petTransport;

    public VehicleDTO(Vehicle vehicle)
    {
        this(vehicle.getId(),
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
