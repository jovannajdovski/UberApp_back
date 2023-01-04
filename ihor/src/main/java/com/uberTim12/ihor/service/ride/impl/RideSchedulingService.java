package com.uberTim12.ihor.service.ride.impl;

import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideReservation;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.ride.interfaces.IRideSchedulingService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.interfaces.IPathService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RideSchedulingService implements IRideSchedulingService {
    @Autowired
    IRideService rideService;
    @Autowired
    IPathService pathService;
    @Autowired
    IPassengerService passengerService;

    @Override
    public Ride findFreeVehicle(CreateRideDTO rideDTO) {
        Ride ride = new Ride(rideDTO);

        Set<Path> paths = new HashSet<>();

        for (PathDTO pdto: rideDTO.getLocations()){
            Path path = new Path();

            Location departure = pdto.getDeparture().generateLocation();
            Location destination = pdto.getDestination().generateLocation();

            path.setStartPoint(departure);
            path.setEndPoint(destination);

            path = pathService.save(path);
            paths.add(path);
        }
        ride.setPaths(paths);

        Set<Passenger> passengers = new HashSet<>();
        for (UserRideDTO udto: rideDTO.getPassengers()){
            Passenger passenger = passengerService.findById(udto.getId());
            passengers.add(passenger);
        }
        ride.setPassengers(passengers);

        return null;
    }

    @Override
    public RideReservation bookRide(CreateRideDTO rideDTO) {
        Set<Passenger> passengers = new HashSet<>();
        for (UserRideDTO udto: rideDTO.getPassengers()){
            Passenger passenger = passengerService.findById(udto.getId());
            passengers.add(passenger);
        }

        Set<Path> paths = new HashSet<>();
        for (PathDTO pdto: rideDTO.getLocations()){
            Path path = new Path();

            Location departure = pdto.getDeparture().generateLocation();
            Location destination = pdto.getDestination().generateLocation();

            path.setStartPoint(departure);
            path.setEndPoint(destination);

            path = pathService.save(path);
            paths.add(path);
        }

        RideReservation rideReservation=new RideReservation(rideDTO);
        rideReservation.setPaths(paths);
        rideReservation.setPassengers(passengers);
        //rideReservation estimatedPrice, Minutes
        rideReservation=rideService.save(rideReservation);
        return rideReservation;
    }
}
