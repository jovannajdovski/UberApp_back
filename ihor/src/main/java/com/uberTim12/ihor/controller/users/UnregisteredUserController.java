package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.ride.RideRequestDTO;
import com.uberTim12.ihor.dto.ride.RideResponseDTO;
import com.uberTim12.ihor.dto.users.UserCredentialsDTO;
import com.uberTim12.ihor.dto.users.UserTokensDTO;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.users.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnregisteredUserController {
    private final RideService rideService;
    private final UserService userService;

    @Autowired
    public UnregisteredUserController(RideService rideService, UserService userService) {
        this.rideService = rideService;
        this.userService = userService;
    }

    @PostMapping(value = "api/unregisteredUser/",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEstimatedRoute(@RequestBody RideRequestDTO rideRequestDTO)
    {
        RideResponseDTO estimatedRoute=rideService.getEstimatedRoute(rideRequestDTO);
        if(estimatedRoute==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        else
            return new ResponseEntity<>(estimatedRoute, HttpStatus.OK);
    }

}