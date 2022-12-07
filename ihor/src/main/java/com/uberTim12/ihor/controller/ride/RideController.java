package com.uberTim12.ihor.controller.ride;

import com.uberTim12.ihor.dto.communication.PanicDTO;
import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.ride.RideDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.PassengerDTO;
import com.uberTim12.ihor.dto.users.PassengerRegistrationDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.communication.Panic;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRejection;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.service.communication.impl.PanicService;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.route.impl.PathService;
import com.uberTim12.ihor.service.users.impl.DriverService;
import com.uberTim12.ihor.service.users.impl.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "api/ride")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private PathService pathService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private PanicService panicService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createRide(@RequestBody CreateRideDTO rideDTO) {

        Ride ride = new Ride();
        ride.getVehicleType().setVehicleCategory(rideDTO.getVehicleType());
        ride.setBabiesAllowed(rideDTO.isBabyTransport());
        ride.setPetsAllowed(rideDTO.isPetTransport());

        Set<Path> paths = new HashSet<>();

        for (PathDTO pdto: rideDTO.getLocations()){
            Path path = new Path();
            path.setStartPoint(pdto.getDeparture());
            path.setEndPoint(pdto.getDestination());

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

        Driver mokapDriver = new Driver();        // mockup data
        mokapDriver.setId(9999);
        mokapDriver.setEmail("mokap@gmail.com");
        ride.setDriver(mokapDriver);
        ride.setStartTime(LocalDateTime.now());
        ride.setEndTime(LocalDateTime.now().plusMinutes(20));
        ride.setTotalPrice(666.0);
        ride.setEstimatedTime(20.0);
        ride.setRideStatus(RideStatus.PENDING);
//        RideRejection mokapRideRejection = new RideRejection();
//        mokapRideRejection.setReason("exit");
//        mokapRideRejection.setTime(LocalDateTime.now());
//        ride.setRideRejection(mokapRideRejection);

        ride = rideService.save(ride);
        return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);

    }

    @GetMapping(value = "/driver/{driverId}/active")
    public ResponseEntity<?> getActiveRideForDriver(@PathVariable Integer driverId) {

        Driver driver = driverService.findById(driverId);

        if (driver == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {
            Ride ride = rideService.findActiveByDriver(driver);
            if (ride == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
            }
        }
    }

    @GetMapping(value = "/passenger/{passengerId}/active")
    public ResponseEntity<?> getActiveRideForPassenger(@PathVariable Integer passengerId) {

        Passenger passenger = passengerService.findById(passengerId);

        if (passenger == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {
            Ride ride = rideService.findActiveByPassenger(passenger);
            if (ride == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
            }
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getRideById(@PathVariable Integer id) {

        Ride ride = rideService.findById(id);

        if (ride == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {
            return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
        }
    }

    @PutMapping(value = "/{id}/withdraw")
    public ResponseEntity<?> cancelRide(@PathVariable Integer id) {

        Ride ride = rideService.findById(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        ride.setRideStatus(RideStatus.CANCELED);

        ride = rideService.save(ride);

        return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/panic")
    public ResponseEntity<?> panicRide(@PathVariable Integer id, @RequestBody String reason) {

        Ride ride = rideService.findById(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        Panic panic = new Panic();
        panic.setCurrentRide(ride);
        panic.setTime(LocalDateTime.now());
        panic.setReason(reason);

        Driver mokapDriver = new Driver();        // mockup data
        mokapDriver.setId(9999);
        mokapDriver.setEmail("mokap@gmail.com");
        mokapDriver.setName("Mika");
        mokapDriver.setSurname("Mikic");
        mokapDriver.setTelephoneNumber("381666666");
        mokapDriver.setAddress("Bulevar Oslobodjenja 1");
        mokapDriver.setProfilePicture("43dkl343lkwecc");

        panic.setUser(mokapDriver);

        panic = panicService.save(panic);
        return new ResponseEntity<>(new PanicDTO(panic), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/accept")
    public ResponseEntity<?> acceptRide(@PathVariable Integer id) {

        Ride ride = rideService.findById(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        ride.setRideStatus(RideStatus.ACCEPTED);

        ride = rideService.save(ride);

        return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/end")
    public ResponseEntity<?> endRide(@PathVariable Integer id) {

        Ride ride = rideService.findById(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        ride.setRideStatus(RideStatus.FINISHED);

        ride = rideService.save(ride);

        return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/cancel")
    public ResponseEntity<?> rejectRide(@PathVariable Integer id, @RequestBody String reason) {

        Ride ride = rideService.findById(id);

        if (ride == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        ride.getRideRejection().setReason(reason);
        ride.getRideRejection().setTime(LocalDateTime.now());
        ride.setRideStatus(RideStatus.REJECTED);

        ride = rideService.save(ride);

        return new ResponseEntity<>(new RideDTO(ride), HttpStatus.OK);
    }


}
