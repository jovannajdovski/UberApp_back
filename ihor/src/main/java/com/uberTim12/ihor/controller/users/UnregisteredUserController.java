package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.ride.RideRequestDTO;
import com.uberTim12.ihor.dto.ride.RideResponseDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.route.impl.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class UnregisteredUserController {
    private final RideService rideService;
    private final PathService pathService;

    @Autowired
    public UnregisteredUserController(RideService rideService, PathService pathService) {
        this.rideService = rideService;
        this.pathService = pathService;
    }

    @PostMapping(value = "api/unregisteredUser/",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEstimatedRoute(@Valid @RequestBody RideRequestDTO rideRequestDTO)
    {
        Ride ride=new Ride(rideRequestDTO);

        Set<Path> paths = new HashSet<>();
        for (PathDTO pathDTO : rideRequestDTO.getLocations()) {
            Path path = new Path();

            Location departure = pathDTO.getDeparture().generateLocation();
            Location destination = pathDTO.getDestination().generateLocation();

            path.setStartPoint(departure);
            path.setEndPoint(destination);

            path = pathService.save(path);
            paths.add(path);
        }
        ride.setPaths(paths);

        RideResponseDTO estimatedRoute = rideService.getEstimatedRoute(ride);

        return new ResponseEntity<>(estimatedRoute, HttpStatus.OK);
    }

}