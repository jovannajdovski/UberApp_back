package com.uberTim12.ihor.service.ride.interfaces;

import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.model.ride.Ride;

public interface IRideSchedulingService {
    Ride findFreeVehicle(Ride ride);
}
