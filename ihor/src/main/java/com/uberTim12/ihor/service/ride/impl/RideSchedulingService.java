package com.uberTim12.ihor.service.ride.impl;

import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.ActiveDriver;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IActiveDriverRepository;
import com.uberTim12.ihor.service.ride.interfaces.IRideSchedulingService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.interfaces.IPathService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RideSchedulingService implements IRideSchedulingService {
    @Autowired
    IRideService rideService;
    @Autowired
    IPathService pathService;
    @Autowired
    IPassengerService passengerService;
    @Autowired
    IVehicleService vehicleService;
    @Autowired
    IDriverService driverService;
    @Autowired
    IActiveDriverRepository activeDriverRepository;

    @Override
    public Ride findFreeVehicle(Ride ride) {

        List<ActiveDriver> activeDrivers=activeDriverRepository.findAll();
        List<ActiveDriver> attainableDrivers=new ArrayList<>();
        for(ActiveDriver activeDriver: activeDrivers)
        {
            if(vehicleService.isVehicleMeetCriteria(activeDriver.getDriver().getVehicle(),ride) && driverService.isDriverAvailable(activeDriver.getDriver(),ride))
            {
                attainableDrivers.add(activeDriver);
            }
        }
        boolean freeDriver=false;
        for(ActiveDriver attainableDriver: attainableDrivers)
        {
            if(driverService.isDriverFreeForRide(attainableDriver.getDriver(),ride))
            {
                freeDriver=true;
                //pamti najmanju udaljenost iz attainabledriver.getLocation
            }
        }
        if(!freeDriver)
        {
            
        }
        return null;
    }


}
