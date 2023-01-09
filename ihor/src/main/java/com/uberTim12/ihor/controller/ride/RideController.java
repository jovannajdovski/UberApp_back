package com.uberTim12.ihor.controller.ride;

import com.uberTim12.ihor.dto.communication.PanicDTO;
import com.uberTim12.ihor.dto.communication.ReasonDTO;
import com.uberTim12.ihor.dto.ride.CreateFavoriteDTO;
import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.ride.FavoriteFullDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.DriverDetailsDTO;
import com.uberTim12.ihor.dto.users.DriverRegistrationDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.exception.*;
import com.uberTim12.ihor.model.communication.Panic;
import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRejection;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import com.uberTim12.ihor.service.communication.impl.PanicService;
import com.uberTim12.ihor.service.communication.interfaces.IPanicService;
import com.uberTim12.ihor.service.ride.impl.FavoriteService;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.ride.interfaces.IFavoriteService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.impl.PathService;
import com.uberTim12.ihor.service.route.interfaces.IPathService;
import com.uberTim12.ihor.service.users.impl.DriverService;
import com.uberTim12.ihor.service.users.impl.PassengerService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.util.ImageConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "api/ride")
public class RideController {

    private final IRideService rideService;

    private final IPathService pathService;

    private final IPassengerService passengerService;

    private final IDriverService driverService;

    private final IPanicService panicService;

    private final IFavoriteService favoriteService;

    @Autowired
    public RideController(RideService rideService, PathService pathService, PassengerService passengerService, DriverService driverService, PanicService panicService, FavoriteService favoriteService) {
        this.rideService = rideService;
        this.pathService = pathService;
        this.passengerService = passengerService;
        this.driverService = driverService;
        this.panicService = panicService;
        this.favoriteService = favoriteService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideFullDTO> createRide(@RequestBody CreateRideDTO rideDTO) {

        Ride ride = new Ride();
        ride.setVehicleType(new VehicleType());
        ride.getVehicleType().setVehicleCategory(rideDTO.getVehicleType());
        ride.setBabiesAllowed(rideDTO.isBabyTransport());
        ride.setPetsAllowed(rideDTO.isPetTransport());

        Set<Path> paths = new HashSet<>();

        for (PathDTO pathDTO : rideDTO.getLocations()) {
            Path path = new Path();

            Location departure = pathDTO.getDeparture().generateLocation();
            Location destination = pathDTO.getDestination().generateLocation();

            path.setStartPoint(departure);
            path.setEndPoint(destination);

            path = pathService.save(path);
            paths.add(path);
        }
        ride.setPaths(paths);

        Set<Passenger> passengers = new HashSet<>();
        for (UserRideDTO userDTO : rideDTO.getPassengers()) {
            Passenger passenger = passengerService.get(userDTO.getId());
            passengers.add(passenger);
        }
        ride.setPassengers(passengers);

        Driver mokapDriver = driverService.get(2);
        ride.setDriver(mokapDriver);
        //ride.setDriver(driverService.findAppropriateForRide(paths)); // TODO

        ride.setStartTime(LocalDateTime.now());
        ride.setEndTime(LocalDateTime.now().plusMinutes(20));
        ride.setTotalPrice(666.0);
        ride.setEstimatedTime(20.0);
        //rideService.calculate(ride);  // TODO

        ride.setRideStatus(RideStatus.PENDING);

        ride = rideService.save(ride);
        return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);

    }

    @GetMapping(value = "/driver/{driverId}/active")
    public ResponseEntity<RideFullDTO> getActiveRideForDriver(@PathVariable Integer driverId) {
        try {
            Driver driver;
            driver = driverService.get(driverId);

            Ride ride = rideService.findActiveByDriver(driver);
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        } catch (NoActiveRideException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active ride does not exist!");
        }
    }

    @GetMapping(value = "/passenger/{passengerId}/active")
    public ResponseEntity<RideFullDTO> getActiveRideForPassenger(@PathVariable Integer passengerId) {
        try {
            Passenger passenger;
            passenger = passengerService.get(passengerId);

            Ride ride = rideService.findActiveByPassenger(passenger);
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger does not exist!");
        } catch (NoActiveRideException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active ride does not exist!");
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RideFullDTO> getRideById(@PathVariable Integer id) {
        try {
            Ride ride = rideService.get(id);
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
    }

    @PutMapping(value = "/{id}/withdraw")
    public ResponseEntity<RideFullDTO> cancelRide(@PathVariable Integer id) {
        try {
            Ride ride = rideService.cancel(id);
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        } catch (RideStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel a ride that is not in status PENDING or STARTED!");
        }
    }

    @PutMapping(value = "/{id}/panic")
    public ResponseEntity<PanicDTO> panicRide(@PathVariable Integer id, @RequestBody ReasonDTO reason) {

        try {
            Ride ride = rideService.get(id);
            Panic panic = panicService.createForRide(ride, reason.getReason());
            return new ResponseEntity<>(new PanicDTO(panic), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!");
        }
    }

    @PutMapping(value = "/{id}/start")
    public ResponseEntity<RideFullDTO> startRide(@PathVariable Integer id) {
        try {
            Ride ride = rideService.start(id);
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        } catch (RideStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot start a ride that is not in status ACCEPTED!");
        }
    }

    @PutMapping(value = "/{id}/accept")
    public ResponseEntity<RideFullDTO> acceptRide(@PathVariable Integer id) {
        try {
            Ride ride = rideService.accept(id);
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        } catch (RideStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot accept a ride that is not in status PENDING!");
        }
    }

    @PutMapping(value = "/{id}/end")
    public ResponseEntity<RideFullDTO> endRide(@PathVariable Integer id) {
        try {
            Ride ride = rideService.end(id);
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        } catch (RideStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot end a ride that is not in status ACTIVE!");
        }
    }

    @PutMapping(value = "/{id}/cancel")
    public ResponseEntity<RideFullDTO> rejectRide(@PathVariable Integer id, @RequestBody ReasonDTO reason) {

        try {
            Ride ride = rideService.reject(id, reason.getReason());
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        } catch (RideStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel a ride that is not in status PENDING!");
        }
    }

    @PostMapping(value = "/favorites", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FavoriteFullDTO> createFavorite(@RequestBody CreateFavoriteDTO favoriteDTO) {
        try {
            Favorite favorite = favoriteService.create(favoriteDTO);
            return new ResponseEntity<>(new FavoriteFullDTO(favorite), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger does not exist!");
        } catch (FavoriteRideExceedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping(value = "/favorites")
    public ResponseEntity<Set<FavoriteFullDTO>> getFavorites() {
        List<Favorite> favorites = favoriteService.getAll();
        Set<FavoriteFullDTO> favoritesDTO = new HashSet<>();
        for (Favorite favorite : favorites) {
            favoritesDTO.add(new FavoriteFullDTO(favorite));
        }
        return new ResponseEntity<>(favoritesDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/favorites/passenger")
    public ResponseEntity<Set<FavoriteFullDTO>> getFavoritesForPassenger() {
        try {
            Set<Favorite> favorites = favoriteService.getForPassenger();
            // if all then favoriteService.getAll();
            Set<FavoriteFullDTO> favoritesDTO = new HashSet<>();
            for (Favorite favorite : favorites) {
                favoritesDTO.add(new FavoriteFullDTO(favorite));
            }
            return new ResponseEntity<>(favoritesDTO, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!");
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied!");
        }
    }

    @DeleteMapping(value = "/favorites/{id}")
    public ResponseEntity<String> deleteFavorite(@PathVariable Integer id) {
        try {
            favoriteService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successful deletion of favorite location!");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite location does not exist!");
        }
    }
}
