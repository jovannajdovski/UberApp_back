package com.uberTim12.ihor.dto.ride;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideFullDTO {

    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endTime;

    private Double totalCost;

    private UserRideDTO driver;

    private Set<UserRideDTO> passengers = new HashSet<>();

    private Double estimatedTimeInMinutes;

    private VehicleCategory vehicleType;
    private boolean babyTransport;

    private boolean petTransport;


    private RideRejectionDTO rejection;

    private Set<PathDTO> locations = new HashSet<>();

    private RideStatus status;
    private LocalDateTime scheduledTime;

    public RideFullDTO(Ride ride){
        this(ride.getId(), ride.getStartTime(), ride.getEndTime(), ride.getTotalPrice(), ride.getEstimatedTime(),
                ride.getVehicleType().getVehicleCategory(), ride.isBabiesAllowed(), ride.isPetsAllowed(), ride.getRideStatus(),ride.getScheduledTime());

        this.driver = new UserRideDTO(ride.getDriver());

        Set<UserRideDTO> passengers = new HashSet<>();
        for (User u : ride.getPassengers()){
            passengers.add(new UserRideDTO(u));
        }
        this.passengers = passengers;

        if (ride.getRideRejection()!=null) {
            this.rejection = new RideRejectionDTO(ride.getRideRejection());
        }

        Set<PathDTO> locations = new HashSet<>();
        for (Path p : ride.getPaths()){
            locations.add(new PathDTO(p));
        }
        this.locations = locations;
    }

    public RideFullDTO(Integer id, LocalDateTime startTime, LocalDateTime endTime, Double totalPrice, Double estimatedTime,
                       VehicleCategory vehicleCategory, boolean babiesAllowed, boolean petsAllowed, RideStatus rideStatus, LocalDateTime scheduledTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalCost = totalPrice;
        this.estimatedTimeInMinutes = estimatedTime;
        this.vehicleType = vehicleCategory;
        this.babyTransport = babiesAllowed;
        this.petTransport = petsAllowed;
        this.status = rideStatus;
        this.scheduledTime=scheduledTime;
    }
}
