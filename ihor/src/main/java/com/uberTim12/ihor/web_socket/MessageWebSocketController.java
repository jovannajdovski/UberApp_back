package com.uberTim12.ihor.web_socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberTim12.ihor.dto.communication.ChatMessageDTO;
import com.uberTim12.ihor.dto.communication.LiveSupportMessageDTO;
import com.uberTim12.ihor.dto.communication.PanicMessageDTO;
import com.uberTim12.ihor.dto.ride.RideNoStatusDTO;
import com.uberTim12.ihor.dto.users.UserPanicDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Map;

@Controller
@Transactional
public class MessageWebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final IRideService rideService;
    private final IUserService userService;

    @Autowired
    public MessageWebSocketController(SimpMessagingTemplate simpMessagingTemplate, IRideService rideService, IUserService userService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.rideService = rideService;
        this.userService = userService;
    }


    @MessageMapping("/send/message/{rideId}/{fromId}/{toId}")
    public ChatMessageDTO rideChat(@DestinationVariable Integer rideId, @DestinationVariable Integer fromId, @DestinationVariable Integer toId, String message) {
        System.out.println("usao");

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(message,fromId,rideId);
        if (rideId != null && fromId != null && toId != null ) {

//            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/ride-chat/" + rideId, chatMessageDTO);
            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/user-chat/"+toId, chatMessageDTO);
        }

        return chatMessageDTO;
    }

    @MessageMapping("/send/panic/{fromId}/{rideId}")
    public void panicChat(@DestinationVariable Integer fromId, @DestinationVariable Integer rideId, String message) {
        try{
            Ride ride=this.rideService.get(rideId);
            User user=this.userService.get(fromId);
            PanicMessageDTO panicMessageDTO = new PanicMessageDTO(message, new UserPanicDTO(user), new RideNoStatusDTO(ride));

            if (fromId != null && fromId != 0 ) {

                this.simpMessagingTemplate.convertAndSend("api/socket-publisher/panic-chat/admin",
                        panicMessageDTO);
            }
        }
        catch(EntityNotFoundException ignored){}

    }

    @MessageMapping("/send/live-support/{fromId}")
    public LiveSupportMessageDTO liveSupportChat(@DestinationVariable Integer fromId, String message) {

        LiveSupportMessageDTO liveSupportMessageDTO = new LiveSupportMessageDTO(message,fromId);
        if (fromId != null && fromId != 0 ) {

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
