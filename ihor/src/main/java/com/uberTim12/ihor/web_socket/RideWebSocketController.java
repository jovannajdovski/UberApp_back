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

//    @CrossOrigin(origins = "http://localhost:4200")
//    @MessageMapping("vehicle/{rideId}/current-location/{authHeader}")
//    public void getVehicleCurrentLocation(@DestinationVariable Integer rideId, @DestinationVariable String authHeader)
//    {
//        System.out.println("usao u soket");
//        String token = authHeader.substring(7);
//        String userId=jwtUtil.extractId(token);
//        try {
//            Ride ride = rideService.get(rideId);
//            if (!userId.equals(ride.getDriver().getId().toString())) {
//
//            }
//            if (!passengerInPassengers(userId, new ArrayList<>(ride.getPassengers())))
//            {
//
//            }    //nesto
//            Location currentLocation=ride.getDriver().getVehicle().getCurrentLocation();
//            System.out.println("salje ");
//            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"vehicle/current-location/"+rideId , new LocationDTO(currentLocation));
//        } catch (EntityNotFoundException e) {
//            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride does not exist!");
//        }
//    }
    private boolean passengerInPassengers(String passengerId, ArrayList<Passenger> passengers) {
        for (Passenger p : passengers)
            if (passengerId.equals(p.getId().toString()))
                return true;
        return false;
    }
    @MessageMapping("/send/message")
    public Map<String, String> broadcastNotification(String message) {
        Map<String, String> messageConverted = parseMessage(message);

        if (messageConverted != null) {
            if (messageConverted.containsKey("toId") && messageConverted.get("toId") != null
                    && !messageConverted.get("toId").equals("")) {
                this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + messageConverted.get("toId"),
                        messageConverted);
                this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + messageConverted.get("fromId"),
                        messageConverted);
            } else {
                this.simpMessagingTemplate.convertAndSend("/socket-publisher", messageConverted);
            }
        }

        return messageConverted;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> retVal;

        try {
            retVal = mapper.readValue(message, Map.class); // parsiranje JSON stringa
        } catch (IOException e) {
            retVal = null;
        }

        return retVal;
    }
}
