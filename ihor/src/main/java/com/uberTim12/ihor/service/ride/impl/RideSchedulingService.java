package com.uberTim12.ihor.service.ride.impl;

import com.uberTim12.ihor.dto.route.RouteStep;
import com.uberTim12.ihor.exception.CannotScheduleDriveException;
import com.uberTim12.ihor.model.ride.ActiveDriver;
import com.uberTim12.ihor.model.ride.ActiveDriverCriticalRide;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.repository.ride.IActiveDriverRepository;
import com.uberTim12.ihor.service.ride.interfaces.IRideSchedulingService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RideSchedulingService implements IRideSchedulingService {
    private final IRideService rideService;
    private final IVehicleService vehicleService;
    private final IDriverService driverService;
    private final IActiveDriverRepository activeDriverRepository;
    private final ILocationService locationService;
    private final IWorkHoursService workHoursService;
    private final IPassengerService passengerService;

    @Autowired
    public RideSchedulingService(IRideService rideService, IVehicleService vehicleService, IDriverService driverService, IActiveDriverRepository activeDriverRepository, ILocationService locationService, IWorkHoursService workHoursService, IPassengerService passengerService) {
        this.rideService = rideService;
        this.vehicleService = vehicleService;
        this.driverService = driverService;
        this.activeDriverRepository = activeDriverRepository;
        this.locationService = locationService;
        this.workHoursService = workHoursService;
        this.passengerService = passengerService;
    }
    private List<ActiveDriver> getQualifiedDrivers(Ride ride) {
        List<ActiveDriver> qualifiedDrivers = new ArrayList<>();
        for(ActiveDriver activeDriver: activeDriverRepository.findAll())
            if(vehicleService.isVehicleMeetCriteria(activeDriver.getDriver().getVehicle(),ride) &&
                    workHoursService.isDriverAvailable(activeDriver.getDriver(),ride))
                qualifiedDrivers.add(activeDriver);

        return qualifiedDrivers;
    }
    private void setEstimatedTimeForRide(Ride ride){
        try{
            ride.setEstimatedTime(locationService.calculateEstimatedTime(
                    ride.getPaths().iterator().next().getStartPoint(),
                    ride.getPaths().iterator().next().getEndPoint()));
        }
        catch(ParseException | IOException e)
        {
            ride.setEstimatedTime(Double.MAX_VALUE);
        }
    }
    private void setTotalPriceForRide(Ride ride){
        Double distance;
        try{
            distance=locationService.calculateDistance(ride.getPaths().iterator().next().getStartPoint(), ride.getPaths().iterator().next().getEndPoint());
        }
        catch(ParseException | IOException e)
        {
            distance=Double.MAX_VALUE;
        }
        ride.setTotalPrice((double)Math.round(ride.getVehicleType().getPricePerKM()+distance*120));

    }
    private Double getDistanceFromDriverToStart(ActiveDriver driver, Ride ride) {
        Double distance;
        try{
            distance=locationService.calculateDistance(
                    ride.getPaths().iterator().next().getStartPoint(),
                    driver.getLocation());
        }
        catch(ParseException | IOException e)
        {
            distance=Double.MAX_VALUE;
        }
        return distance;
    }
    private Driver findClosestDriver(List<ActiveDriver> qualifiedDrivers, Ride ride) {
        Double minDistance = Double.MAX_VALUE;
        Double distance;
        Driver foundDriver = null;
        for(ActiveDriver driver: qualifiedDrivers)
        {
            if(driverService.isDriverFreeForRide(driver.getDriver(), ride) &&
                    driver.getDriver().getVehicle().getSeats() > ride.getPassengers().size())
            {
                distance = getDistanceFromDriverToStart(driver, ride);
                if(distance < minDistance) {
                    minDistance = distance;
                    foundDriver = driver.getDriver();
                }
            }
        }

        return foundDriver;
    }
    private Driver findDriverInNextHalfHour(List<ActiveDriver> qualifiedDrivers, Ride ride){
        List<ActiveDriverCriticalRide> qualifiedDriversSorted=driverService.sortPerEndOfCriticalRide(qualifiedDrivers, ride);;
        Driver foundDriver = null;
        for(ActiveDriverCriticalRide qualifiedDriver: qualifiedDriversSorted) {

            Ride criticalRide=qualifiedDriver.getCriticalRide();
            ride.setStartTime(criticalRide.getStartTime().plusMinutes(criticalRide.getEstimatedTime().longValue()));

            if (driverService.isDriverFreeForRide(qualifiedDriver.getDriver(), ride) && qualifiedDriver.getDriver().getVehicle().getSeats()>ride.getPassengers().size()) {
                return qualifiedDriver.getDriver();
            }
        }
        return null;
    }
    private void setRideFinalDetails(Driver driver, Ride ride){
        ride.setDriver(driver);
        ride.setRideStatus(RideStatus.PENDING);
        ride.setVehicleType(driver.getVehicle().getVehicleType());
    }
    @Override
    public Ride findFreeVehicle(Ride ride) throws CannotScheduleDriveException {
        setEstimatedTimeForRide(ride);
        
        if(!passengerService.isPassengersFree(ride))
            throw new CannotScheduleDriveException("Driving is not possible!");

        List<ActiveDriver> qualifiedDrivers=getQualifiedDrivers(ride);

        Driver driver=findClosestDriver(qualifiedDrivers,ride);
        
        if(driver!=null)
            setRideFinalDetails(driver,ride);
        else{
            driver = findDriverInNextHalfHour(qualifiedDrivers, ride);
            if(driver!=null)
                setRideFinalDetails(driver,ride);
            else
                throw new CannotScheduleDriveException("Driving is not possible!");
        }
        setTotalPriceForRide(ride);
        rideService.save(ride);
        return ride;
    }
}
