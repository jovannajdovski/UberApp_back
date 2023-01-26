package com.uberTim12.ihor.controller.ride;

import com.uberTim12.ihor.dto.ResponseMessageDTO;
import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.communication.PanicDTO;
import com.uberTim12.ihor.dto.communication.ReasonDTO;
import com.uberTim12.ihor.dto.ride.*;
import com.uberTim12.ihor.dto.route.FavoriteRouteForPassengerDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.exception.*;
import com.uberTim12.ihor.model.communication.Panic;
import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.service.communication.impl.PanicService;
import com.uberTim12.ihor.service.communication.interfaces.IPanicService;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.ride.interfaces.IRideSchedulingService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.impl.PathService;
import com.uberTim12.ihor.service.ride.interfaces.IFavoriteService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import com.uberTim12.ihor.service.route.interfaces.IPathService;
import com.uberTim12.ihor.service.users.impl.DriverService;
import com.uberTim12.ihor.service.users.impl.PassengerService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import com.uberTim12.ihor.util.RideSimulationTimer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "api/ride")
public class RideController {
    private final IRideService rideService;
    private final IVehicleService vehicleService;
    private final IPathService pathService;
    private final IPassengerService passengerService;
    private final IDriverService driverService;
    private final IPanicService panicService;
    private final ILocationService locationService;
    private final IRideSchedulingService rideSchedulingService;
    private final IFavoriteService favoriteService;
    private final JwtUtil jwtUtil;

    @Autowired
    public RideController(RideService rideService, IVehicleService vehicleService, PathService pathService, PassengerService passengerService,
                          DriverService driverService, PanicService panicService,
                          ILocationService locationService, IRideSchedulingService rideSchedulingService, IFavoriteService favoriteService, JwtUtil jwtUtil) {
        this.rideService = rideService;
        this.vehicleService = vehicleService;
        this.pathService = pathService;
        this.passengerService = passengerService;
        this.driverService = driverService;
        this.panicService = panicService;
        this.locationService = locationService;
        this.rideSchedulingService = rideSchedulingService;
        this.favoriteService = favoriteService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> createRide(@Valid @RequestBody CreateRideDTO rideDTO) {
        Ride ride = new Ride(rideDTO);
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

        try {
            ride=rideSchedulingService.findFreeVehicle(ride);
        } catch (CannotScheduleDriveException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
    }

    @GetMapping(value = "/driver/{driverId}/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getActiveRideForDriver(@Min(value = 1) @PathVariable Integer driverId,
                                                    @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        if (jwtUtil.extractRole(token).equals("ROLE_DRIVER") &&
                !jwtUtil.extractId(token).equals(driverId.toString()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Active ride does not exist!");

        try {
            Driver driver;
            driver = driverService.get(driverId);

            Ride ride = rideService.findActiveByDriver(driver);

            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver does not exist!");
        } catch (NoActiveRideException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Active ride does not exist!");
        }
    }

    @GetMapping(value = "/driver/{driverId}/accepted")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getAcceptedRidesForDriver(@Min(value = 1) @PathVariable Integer driverId,
                                                    @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        if (jwtUtil.extractRole(token).equals("ROLE_DRIVER") &&
                !jwtUtil.extractId(token).equals(driverId.toString()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Active ride does not exist!");

        try {
            Driver driver;
            driver = driverService.get(driverId);

            List<Ride> rides = rideService.findAcceptedByDriver(driver);

            List<RideNoStatusDTO> rideDTOs = new ArrayList<>();
            for (Ride r : rides)
                rideDTOs.add(new RideNoStatusDTO(r));

            ObjectListResponseDTO<RideNoStatusDTO> res = new ObjectListResponseDTO<>((int) rides.size(), rideDTOs);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver does not exist!");
        } catch (NoActiveRideException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Accepted ride does not exist!");
        }
    }

    @GetMapping(value = "/passenger/{passengerId}/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public ResponseEntity<?> getActiveRideForPassenger(@Min(value = 1) @PathVariable Integer passengerId,
                                                       @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        if (jwtUtil.extractRole(token).equals("ROLE_PASSENGER") &&
                !jwtUtil.extractId(token).equals(passengerId.toString()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Active ride does not exist!");

        try {
            Passenger passenger;
            passenger = passengerService.get(passengerId);

            Ride ride = rideService.findActiveByPassenger(passenger);

            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");
        } catch (NoActiveRideException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Active ride does not exist!");
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getRideById(@Min(value = 1) @PathVariable Integer id,
                                         @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        try {
            Ride ride = rideService.get(id);


            if (jwtUtil.extractRole(token).equals("ROLE_DRIVER") &&
                    !jwtUtil.extractId(token).equals(ride.getDriver().getId().toString()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
            else if (jwtUtil.extractRole(token).equals("ROLE_PASSENGER") &&
                    !passengerInPassengers(jwtUtil.extractId(token), new ArrayList<>(ride.getPassengers())))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");

            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
        }
    }
    @PutMapping(value = "/specific-rides", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRidesById(@RequestBody RideIdListDTO rideIdListDTO) {

        List<RideFullDTO> rideFullDTOS=new ArrayList<>();
        try {
            for(int id:rideIdListDTO.getIds())
            {
                Ride ride = rideService.get(id);
                rideFullDTOS.add(new RideFullDTO(ride));

            }

            ObjectListResponseDTO<RideFullDTO> res=new ObjectListResponseDTO<>(rideFullDTOS.size(),rideFullDTOS);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
        }
    }

    @PutMapping(value = "/{id}/withdraw")
    @PreAuthorize("hasRole('DRIVER') or hasRole('PASSENGER')")
    public ResponseEntity<?> cancelRide(@Min(value = 1) @PathVariable Integer id,
                                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        try {
            Ride ride = rideService.get(id);
            if (jwtUtil.extractRole(token).equals("ROLE_DRIVER") &&
                    !jwtUtil.extractId(token).equals(ride.getDriver().getId().toString()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
            else if (jwtUtil.extractRole(token).equals("ROLE_PASSENGER") &&
                    !passengerInPassengers(jwtUtil.extractId(token), new ArrayList<>(ride.getPassengers())))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");

            ride = rideService.cancel(id);
            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
        } catch (RideStatusException e) {
            return new ResponseEntity<>(new ResponseMessageDTO("Cannot cancel a ride that is not in status PENDING or STARTED!"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/panic")
    public ResponseEntity<?> panicRide(@Min(value = 1) @PathVariable Integer id,
                                       @Valid @RequestBody ReasonDTO reason,
                                       @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        try {
            Ride ride = rideService.get(id);
            if (jwtUtil.extractRole(token).equals("ROLE_DRIVER") &&
                    !jwtUtil.extractId(token).equals(ride.getDriver().getId().toString()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
            else if (jwtUtil.extractRole(token).equals("ROLE_PASSENGER") &&
                    !passengerInPassengers(jwtUtil.extractId(token), new ArrayList<>(ride.getPassengers())))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");


            Panic panic = panicService.createForRide(ride, reason.getReason());
            return new ResponseEntity<>(new PanicDTO(panic), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @PutMapping(value = "/{id}/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> startRide(@Min(value = 1) @PathVariable Integer id,
                                       @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        try {
            Ride ride = rideService.start(id, Integer.parseInt(jwtUtil.extractId(token)));
            if (!jwtUtil.extractId(token).equals(ride.getDriver().getId().toString()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");

            new Timer().scheduleAtFixedRate(new RideSimulationTimer(ride.getDriver().getVehicle().getId(),vehicleService,
                            locationService.getSteps(ride.getPaths().iterator().next().getStartPoint(),
                                    ride.getPaths().iterator().next().getEndPoint())),
                    0,2000);

            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
        } catch (RideStatusException e) {
            return new ResponseEntity<>(new ResponseMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(value = "/{id}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> acceptRide(@Min(value = 1) @PathVariable Integer id,
                                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        try {
            Ride ride = rideService.accept(id);
            if (!jwtUtil.extractId(token).equals(ride.getDriver().getId().toString()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");

            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
        } catch (RideStatusException e) {
            return new ResponseEntity<>(new ResponseMessageDTO("Cannot accept a ride that is not in status PENDING!"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/end")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> endRide(@Min(value = 1) @PathVariable Integer id,
                                     @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        try {
            Ride ride = rideService.end(id);
            if (!jwtUtil.extractId(token).equals(ride.getDriver().getId().toString()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");

            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
        } catch (RideStatusException e) {
            return new ResponseEntity<>(new ResponseMessageDTO("Cannot end a ride that is not in status STARTED!"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/cancel")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> rejectRide(@Min(value = 1) @PathVariable Integer id, @Valid @RequestBody ReasonDTO reason,
                                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        try {
            Ride ride = rideService.reject(id, reason.getReason());
            if (!jwtUtil.extractId(token).equals(ride.getDriver().getId().toString()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");

            return new ResponseEntity<>(new RideFullDTO(ride), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
        } catch (RideStatusException e) {
            return new ResponseEntity<>(new ResponseMessageDTO("Cannot cancel a ride that is not in status PENDING or ACCEPTED!"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/favorites", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public ResponseEntity<?> createFavorite(@Min(value = 1) @Valid @RequestBody CreateFavoriteDTO favoriteDTO,
                                            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        try {
            Favorite favorite = favoriteService.create(favoriteDTO);
            if (jwtUtil.extractRole(token).equals("ROLE_PASSENGER") &&
                    !passengerInDTOs(jwtUtil.extractId(token), new ArrayList<>(favoriteDTO.getPassengers())))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");

            return new ResponseEntity<>(new FavoriteFullDTO(favorite), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");
        } catch (FavoriteRideExceedException e) {
            return new ResponseEntity<>(new ResponseMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/favorites")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<FavoriteFullDTO>> getFavorites() {
        List<Favorite> favorites = favoriteService.getAll();
        Set<FavoriteFullDTO> favoritesDTO = new HashSet<>();
        for (Favorite favorite : favorites) {
            favoritesDTO.add(new FavoriteFullDTO(favorite));
        }
        return new ResponseEntity<>(favoritesDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/favorites/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public ResponseEntity<?> getFavoritesForPassenger(@PathVariable Integer id,
                                                      @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        if (jwtUtil.extractRole(token).equals("ROLE_PASSENGER") && !id.toString().equals(jwtUtil.extractId(token)))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorites do not exist!");
        try {
            passengerService.get(id);

            List<Favorite> favorites = favoriteService.getForPassenger(id);
            List<FavoriteFullDTO> favoritesDTO = new ArrayList<>();
            for (Favorite favorite : favorites) {
                favoritesDTO.add(new FavoriteFullDTO(favorite));
            }
            return new ResponseEntity<>(favoritesDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");
        }
    }

    @GetMapping(value = "/favorites/passenger/ride")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public ResponseEntity<?> isFavoritesForPassenger(@RequestParam String from,
                                                      @RequestParam String to,
                                                      @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Integer passengerId = Integer.parseInt(jwtUtil.extractId(token));
        try {
            FavoriteRouteForPassengerDTO isFavorite = favoriteService.isFavoriteRouteForPassenger(from,to,passengerId);
            return new ResponseEntity<>(isFavorite, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorite location does not exist!");
        }
    }

    @DeleteMapping(value = "/favorites/{id}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<String> deleteFavorite(@Min(value = 1) @PathVariable Integer id,
                                                 @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        try {
            Favorite favorite = favoriteService.get(id);
            if (!passengerInPassengers(jwtUtil.extractId(token), new ArrayList<>(favorite.getPassengers())))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorite location does not exist!");
            favoriteService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successful deletion of favorite location!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorite location does not exist!");
        }
    }

    private boolean passengerInPassengers(String passengerId, ArrayList<Passenger> passengers) {
        for (Passenger p : passengers)
            if (passengerId.equals(p.getId().toString()))
                return true;
        return false;
    }

    private boolean passengerInDTOs(String passengerId, ArrayList<UserRideDTO> passengers) {
        for (UserRideDTO p : passengers)
            if (passengerId.equals(p.getId().toString()))
                return true;
        return false;
    }
}
