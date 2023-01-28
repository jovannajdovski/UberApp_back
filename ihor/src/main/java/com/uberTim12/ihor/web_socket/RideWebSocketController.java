package com.uberTim12.ihor.web_socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberTim12.ihor.dto.communication.ChatMessageDTO;
import com.uberTim12.ihor.dto.communication.LiveSupportMessageDTO;
import com.uberTim12.ihor.dto.communication.PanicMessageDTO;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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
    public void getVehicleCurrentLocation(@DestinationVariable Integer rideId, @DestinationVariable String token) {
        System.out.println("usao u soket");
        String userId = jwtUtil.extractId(token);
        try {
            Ride ride = rideService.get(rideId);
            if (!userId.equals(ride.getDriver().getId().toString())) {

            }
            if (!passengerInPassengers(userId, new ArrayList<>(ride.getPassengers()))) {

            }    //nesto
            Location currentLocation = ride.getDriver().getVehicle().getCurrentLocation();
            System.out.println("salje ");
            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" + "vehicle/current-location/" + rideId, new LocationDTO(currentLocation));
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

    @MessageMapping("/send/message/{rideId}/{fromId}")
    public ChatMessageDTO rideChat(@DestinationVariable Integer rideId, @DestinationVariable Integer fromId, String message) {

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(message,fromId,rideId);
        if (rideId != null && rideId != 0 && fromId != null && fromId !=0 ) {

            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/ride-chat/" + rideId,
                    chatMessageDTO);
        }

        return chatMessageDTO;
    }

    @MessageMapping("/send/panic/{fromId}")
    public PanicMessageDTO panicChat(@DestinationVariable Integer fromId, String message) {

        PanicMessageDTO panicMessageDTO = new PanicMessageDTO(message,fromId);
        if (fromId != null && fromId != 0 ) {

            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/panic-chat/" + fromId,
                    panicMessageDTO);

            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/panic-chat/admin",
                    panicMessageDTO);
        }

        return panicMessageDTO;
    }

    @MessageMapping("/send/live-support/{fromId}")
    public LiveSupportMessageDTO livSupportChat(@DestinationVariable Integer fromId, String message) {

        LiveSupportMessageDTO liveSupportMessageDTO = new LiveSupportMessageDTO(message,fromId);
        if (fromId != null && fromId != 0 ) {

            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/live-support-chat/" + fromId,
                    liveSupportMessageDTO);

            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/live-support-chat/admin",
                    liveSupportMessageDTO);
        }

        return liveSupportMessageDTO;
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
