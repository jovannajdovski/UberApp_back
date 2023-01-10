package com.uberTim12.ihor.service.ride.impl;

import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.ActiveDriver;
import com.uberTim12.ihor.model.ride.ActiveDriverCriticalRide;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IActiveDriverRepository;
import com.uberTim12.ihor.service.ride.interfaces.IRideSchedulingService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import com.uberTim12.ihor.service.route.interfaces.IPathService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Autowired
    ILocationService locationService;
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
        Double minDistance=Double.MAX_VALUE, distance;
        for(ActiveDriver attainableDriver: attainableDrivers)
        {
            if(driverService.isDriverFreeForRide(attainableDriver.getDriver(),ride))
            {
                freeDriver=true;
                try{
                    distance=locationService.calculateDistance(ride.getPaths().iterator().next().getStartPoint(), attainableDriver.getLocation());
                }
                catch(ParseException | IOException e)
                {
                    distance=Double.MAX_VALUE;
                }
                if(distance<minDistance) {
                    minDistance = distance;
                    ride.setDriver(attainableDriver.getDriver());
                    ride.setRideStatus(RideStatus.PENDING);
                    ride.setVehicleType(ride.getDriver().getVehicle().getVehicleType());
                }
            }
        }
        // nema slobodnog vozaca u tacno to vreme, trazi se u narednih pola sata
        List<ActiveDriverCriticalRide> attainableDriversSorted;
        if(!freeDriver)
        {
            attainableDriversSorted=driverService.sortPerEndOfCriticalRide(attainableDrivers, ride);
            for(ActiveDriverCriticalRide attainableDriver: attainableDriversSorted) {

                Ride criticalRide=attainableDriver.getCriticalRide();
                ride.setStartTime(criticalRide.getStartTime().plusMinutes(criticalRide.getEstimatedTime().longValue()));

                if (driverService.isDriverFreeForRide(attainableDriver.getDriver(), ride)) {
                    freeDriver=true;
                    ride.setDriver(attainableDriver.getDriver());
                    ride.setRideStatus(RideStatus.PENDING);
                    ride.setVehicleType(ride.getDriver().getVehicle().getVehicleType());
                    break;
                }
            }
        }

        if(!freeDriver)
            return null;
        try{
            distance=locationService.calculateDistance(ride.getPaths().iterator().next().getStartPoint(), ride.getPaths().iterator().next().getEndPoint());
        }
        catch(ParseException | IOException e)
        {
            distance=Double.MAX_VALUE;
        }
        ride.setTotalPrice(ride.getVehicleType().getPricePerKM()+distance*120);
        rideService.save(ride);
        return ride;
    }
}
