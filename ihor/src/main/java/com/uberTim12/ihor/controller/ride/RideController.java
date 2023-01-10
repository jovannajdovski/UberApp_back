package com.uberTim12.ihor.controller.ride;

import com.uberTim12.ihor.dto.communication.PanicDTO;
import com.uberTim12.ihor.dto.communication.ReasonDTO;
import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.exception.CannotScheduleDriveException;
import com.uberTim12.ihor.model.communication.Panic;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRejection;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.service.communication.impl.PanicService;
import com.uberTim12.ihor.service.communication.interfaces.IPanicService;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.ride.interfaces.IRideSchedulingService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.impl.PathService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import com.uberTim12.ihor.service.route.interfaces.IPathService;
import com.uberTim12.ihor.service.users.impl.DriverService;
import com.uberTim12.ihor.service.users.impl.PassengerService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import jakarta.persistence.EntityNotFoundException;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "api/ride")
public class RideController {
    private final IRideService rideService;
    private final IPathService pathService;
    private final IPassengerService passengerService;
    private final IDriverService driverService;
    private final IPanicService panicService;
    private final IRideSchedulingService rideSchedulingService;

    @Autowired
    public RideController(RideService rideService, PathService pathService, PassengerService passengerService,
                          DriverService driverService, PanicService panicService,
                          IRideSchedulingService rideSchedulingService) {
        this.rideService = rideService;
        this.pathService = pathService;
        this.passengerService = passengerService;
        this.driverService = driverService;
        this.panicService = panicService;
        this.rideSchedulingService = rideSchedulingService;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<RideFullDTO> createRide(@RequestBody CreateRideDTO rideDTO) {
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
            Passenger passenger = passengerService.get(udto.getId());
            passengers.add(passenger);
        }
        ride.setPassengers(passengers);

        try {
            ride=rideSchedulingService.findFreeVehicle(ride);
        } catch (CannotScheduleDriveException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
    }

    @GetMapping(value = "/driver/{driverId}/active")
    public ResponseEntity<?> getActiveRideForDriver(@PathVariable Integer driverId) {

        if (driverId == 1)
            driverId++;

        Driver driver = driverService.get(driverId);

        if (driver == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {
            Ride ride = rideService.findActiveByDriver(driver);
            if (ride == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
            }
        }
    }

    @GetMapping(value = "/passenger/{passengerId}/active")
    public ResponseEntity<?> getActiveRideForPassenger(@PathVariable Integer passengerId) {

        Passenger passenger = passengerService.get(passengerId);

        if (passenger == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {
            Ride ride = rideService.findActiveByPassenger(passenger);
            if (ride == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
            }
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getRideById(@PathVariable Integer id) {

        Ride ride = rideService.get(id);

        if (ride == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        }
    }

    @PutMapping(value = "/{id}/withdraw")
    public ResponseEntity<?> cancelRide(@PathVariable Integer id) {

        Ride ride = rideService.get(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        ride.setRideStatus(RideStatus.CANCELED);

        ride = rideService.save(ride);

        return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/panic")
    public ResponseEntity<?> panicRide(@PathVariable Integer id, @RequestBody ReasonDTO reason) {

        Ride ride = rideService.get(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        Panic panic = new Panic();
        panic.setCurrentRide(ride);
        panic.setTime(LocalDateTime.now());
        panic.setReason(reason.getReason());

        Driver driver = ride.getDriver();

        panic.setUser(driver);

        panic = panicService.save(panic);
        return new ResponseEntity<>(new PanicDTO(panic), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/accept")
    public ResponseEntity<?> acceptRide(@PathVariable Integer id) {

        Ride ride = rideService.get(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        ride.setRideStatus(RideStatus.ACCEPTED);

        ride = rideService.save(ride);

        return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/end")
    public ResponseEntity<?> endRide(@PathVariable Integer id) {

        Ride ride = rideService.get(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        ride.setRideStatus(RideStatus.FINISHED);

        ride = rideService.save(ride);

        return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/cancel")
    public ResponseEntity<?> rejectRide(@PathVariable Integer id, @RequestBody ReasonDTO reason) {

    Ride ride = rideService.get(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        if(ride.getRideRejection()==null){
            ride.setRideRejection(new RideRejection());
        }

        ride.getRideRejection().setReason(reason.getReason());
        ride.getRideRejection().setTime(LocalDateTime.now());
        ride.setRideStatus(RideStatus.REJECTED);

        ride = rideService.save(ride);

        return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
    }


}
