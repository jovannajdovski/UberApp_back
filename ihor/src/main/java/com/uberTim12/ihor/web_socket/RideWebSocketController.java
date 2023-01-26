package com.uberTim12.ihor.web_socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@Controller
@Transactional
public class RideWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final IVehicleService vehicleService;
    private final JwtUtil jwtUtil;
    private final IRideService rideService;

    public RideWebSocketController(SimpMessagingTemplate simpMessagingTemplate, IVehicleService vehicleService, JwtUtil jwtUtil, IRideService rideService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.vehicleService = vehicleService;
        this.jwtUtil = jwtUtil;
        this.rideService = rideService;
    }

    @MessageMapping("vehicle/{rideId}/current-location/{token}")
    public void getVehicleCurrentLocation(@DestinationVariable Integer rideId, @DestinationVariable String token)
    {
        System.out.println("usao u soket");
        String userId=jwtUtil.extractId(token);
        try {
            Ride ride = rideService.get(rideId);
            if (!userId.equals(ride.getDriver().getId().toString())) {

            }
            if (!passengerInPassengers(userId, new ArrayList<>(ride.getPassengers())))
            {

            }    //nesto
            Location currentLocation=ride.getDriver().getVehicle().getCurrentLocation();
            System.out.println("salje ");
            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"vehicle/current-location/"+rideId , new LocationDTO(currentLocation));
        } catch (EntityNotFoundException e) {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
        }
    }
    private boolean passengerInPassengers(String passengerId, ArrayList<Passenger> passengers) {
        for (Passenger p : passengers)
            if (passengerId.equals(p.getId().toString()))
                return true;
        return false;
    }

}
