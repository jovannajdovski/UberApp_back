package com.uberTim12.ihor.model.vehicle;

import com.uberTim12.ihor.model.comunication.Review;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.DriverDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VehicleDTO {

    private Integer id;
    private Driver driver;
    private String vehicleModel;
    private VehicleType vehicleType;
    private String registrationPlate;
    private Integer seats;
    private Location currentLocation;
    private boolean babiesAllowed;
    private boolean petsAllowed;
    private Set<Review> reviews;

    public VehicleDTO(Vehicle vehicle)
    {
        this(vehicle.getId(),
                vehicle.getDriver(),
                vehicle.getVehicleModel(),
                vehicle.getVehicleType(),
                vehicle.getRegistrationPlate(),
                vehicle.getSeats(),
                vehicle.getCurrentLocation(),
                vehicle.isBabiesAllowed(),
                vehicle.isPetsAllowed(),
                vehicle.getReviews()
        );
    }

}
