package com.uberTim12.ihor.service.ride.interfaces;

import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideReservation;

public interface IRideSchedulingService {
    Ride findFreeVehicle(CreateRideDTO rideDTO);
    RideReservation bookRide(CreateRideDTO rideDTO);
}
