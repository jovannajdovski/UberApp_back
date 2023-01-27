package com.uberTim12.ihor.web_socket;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import com.uberTim12.ihor.util.SocketTimer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
@Transactional
public class RideWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final IVehicleService vehicleService;
    private final JwtUtil jwtUtil;
    private final IRideService rideService;
    private final SocketTimer socketTimer;

    @Autowired
    public RideWebSocketController(SimpMessagingTemplate simpMessagingTemplate, IVehicleService vehicleService, JwtUtil jwtUtil, IRideService rideService, SocketTimer socketTimer) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.vehicleService = vehicleService;
        this.jwtUtil = jwtUtil;
        this.rideService = rideService;
        this.socketTimer = socketTimer;
    }

    @MessageMapping("vehicle/{rideId}/current-location/{token}")
    public void getVehicleCurrentLocation(@DestinationVariable Integer rideId, @DestinationVariable String token)
    {
        System.out.println("usao u soket");
        String userId=jwtUtil.extractId(token);
        try {
            Ride ride = rideService.get(rideId);
//            System.out.println(ride);
//            System.out.println(ride.getId());
//            System.out.println(this.rideService);
            if (!userId.equals(ride.getDriver().getId().toString())) {

            }
            if (!passengerInPassengers(userId, new ArrayList<>(ride.getPassengers())))
            {

            }



//            Timer timer = new Timer();
//            SimpMessagingTemplate smt=this.simpMessagingTemplate;
//            timer.scheduleAtFixedRate(new TimerTask()
//            {
//                public void run()
//                {
//                    // Your code
//
//                    Location currentLocation=ride.getDriver().getVehicle().getCurrentLocation();
//                    System.out.println(currentLocation.getLatitude());
//                    smt.convertAndSend("api/socket-publisher/" +"vehicle/current-location/"+ride.getId() , new LocationDTO(currentLocation));
//
//
//                }
//            }, 0, 2000);
//
            socketTimer.setProperties(ride.getId(),this.simpMessagingTemplate);
//            new Timer().scheduleAtFixedRate(new SocketTimer(this.simpMessagingTemplate,rideService,ride.getId()),0,2000);

            new Timer().scheduleAtFixedRate(socketTimer,0,2000);


//            Location loc1=ride.getDriver().getVehicle().getCurrentLocation();
//            Thread.sleep(5000);
//            Ride ride1 = rideService.get(rideId);
//            System.out.println(ride);
//            System.out.println(ride1);
//            Location loc2=ride1.getDriver().getVehicle().getCurrentLocation();
//            System.out.println(loc1.getLatitude());
//            System.out.println(loc2.getLatitude());

//            while(true)
//            {
//                Thread.sleep(2000);
//                ride=rideService.get(rideId);
//                Location currentLocation=ride.getDriver().getVehicle().getCurrentLocation();
//                this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"vehicle/current-location/"+rideId , new LocationDTO(currentLocation));
//
//            }

//            Location currentLocation=ride.getDriver().getVehicle().getCurrentLocation();
//            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"vehicle/current-location/"+rideId , new LocationDTO(currentLocation));
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
