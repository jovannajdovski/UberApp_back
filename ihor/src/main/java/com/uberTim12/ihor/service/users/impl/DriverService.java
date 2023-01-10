package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.ride.ActiveDriver;
import com.uberTim12.ihor.model.ride.ActiveDriverCriticalRide;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DriverService implements IDriverService {

    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IWorkHoursService workHoursService;
    @Autowired
    private IRideService rideService;
    @Override
    public Driver save(Driver driver) {
        return driverRepository.save(driver);
    }
    @Override
    public Driver findOne(Integer id) {
        return driverRepository.findById(id).orElse(null);
    }

    @Override
    public List<Driver> findAll() { return driverRepository.findAll(); }

    @Override
    public Driver findByEmail(String email) {
        return driverRepository.findByEmail(email);
    }
    @Override
    public Driver findOneWithDocuments(Integer driverId) {
        return driverRepository.findOneWithDocuments(driverId);
    }
    @Override
    public Vehicle getVehicleFor(Integer driverId) {
        Driver driver = findOne(driverId);
        if (driver != null)
            return driver.getVehicle();

        return null;
    }
    @Override
    public Page<Driver> findAll(Pageable page) {
        return driverRepository.findAll(page);
    }

    @Override
    public Driver findById(Integer id){
        return driverRepository.findById(id).orElse(null);
    }

    @Override
    public boolean isDriverAvailable(Driver driver, Ride ride) {
        Long d1=workHoursService.getWorkingMinutesByDriverAtChoosedDay(driver.getId(), LocalDate.now());
        Double d2=rideService.getTimeOfNextRidesByDriverAtChoosedDay(driver.getId(),LocalDate.now());
        Double d3=ride.getEstimatedTime();
        return workHoursService.getWorkingMinutesByDriverAtChoosedDay(driver.getId(), LocalDate.now())
                + rideService.getTimeOfNextRidesByDriverAtChoosedDay(driver.getId(),LocalDate.now())
                + ride.getEstimatedTime() <= 8 * 60;
    }

    @Override
    public boolean isDriverFreeForRide(Driver driver, Ride newRide) {
        LocalDateTime newRideStart=newRide.getStartTime();
        LocalDateTime newRideEnd=newRide.getStartTime().plusMinutes(newRide.getEstimatedTime().longValue());
        LocalDateTime rideStart, rideEnd;
        for(Ride ride: driver.getRides())
        {
            rideStart=ride.getStartTime();
            rideEnd=rideStart.plusMinutes(ride.getEstimatedTime().longValue());
            if(rideService.hasIntersectionBetweenRides(rideStart, rideEnd, newRideStart,newRideEnd) &&
                    (ride.getRideStatus()== RideStatus.ACCEPTED || ride.getRideStatus()==RideStatus.ACTIVE))
                return false;
        }
        return true;
    }
    @Override
    public List<ActiveDriverCriticalRide> sortPerEndOfCriticalRide(List<ActiveDriver> activeDrivers, Ride newRide)
    {
        Ride criticalRide;
        List<ActiveDriverCriticalRide> activeDriversCriticalRides=new ArrayList<>();
        for(ActiveDriver activeDriver:activeDrivers)
        {
            criticalRide=rideService.findCriticalRide(activeDriver.getDriver().getRides(),newRide);
            activeDriversCriticalRides.add(new ActiveDriverCriticalRide(activeDriver,criticalRide));

        }
        activeDriversCriticalRides.sort(RideEndComparator);
        return activeDriversCriticalRides;
    }

    public static Comparator<ActiveDriverCriticalRide> RideEndComparator=new Comparator<ActiveDriverCriticalRide>() {
        @Override
        public int compare(ActiveDriverCriticalRide o1, ActiveDriverCriticalRide o2) {
            LocalDateTime end1=o1.getCriticalRide().getStartTime().plusMinutes(o1.getCriticalRide().getEstimatedTime().longValue());
            LocalDateTime end2=o2.getCriticalRide().getStartTime().plusMinutes(o2.getCriticalRide().getEstimatedTime().longValue());;
            return end1.compareTo(end2);
        }
    };

}
