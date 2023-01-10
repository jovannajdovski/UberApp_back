package com.uberTim12.ihor.service.communication.interfaces;

import com.uberTim12.ihor.exception.UnauthorizedException;
import com.uberTim12.ihor.model.communication.Panic;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;

import java.util.List;

public interface IPanicService extends IJPAService<Panic> {

    List<Panic> findAll();

    Panic createForRide(Ride ride, String reason) throws UnauthorizedException;
}
