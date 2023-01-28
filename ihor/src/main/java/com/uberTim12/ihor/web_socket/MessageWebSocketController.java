package com.uberTim12.ihor.web_socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberTim12.ihor.dto.ResponseMessageDTO;
import com.uberTim12.ihor.dto.communication.ChatMessageDTO;
import com.uberTim12.ihor.dto.communication.LiveSupportMessageDTO;
import com.uberTim12.ihor.dto.communication.MessageDTO;
import com.uberTim12.ihor.dto.communication.PanicMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Map;

@Controller
public class MessageWebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public MessageWebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    @MessageMapping("/send/message/{rideId}/{fromId}/{toId}")
    public ChatMessageDTO rideChat(@DestinationVariable Integer rideId, @DestinationVariable Integer fromId, @DestinationVariable Integer toId, String message) {
        System.out.println("usao");

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(message,fromId,rideId);
        if (rideId != null && rideId != 0 && fromId != null && fromId !=0 && toId != null && toId !=0) {

//            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/ride-chat/" + rideId, chatMessageDTO);
            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/user-chat/"+toId, chatMessageDTO);
        }

        return chatMessageDTO;
    }

    @MessageMapping("/send/panic/{fromId}/{rideId}")
    public PanicMessageDTO panicChat(@DestinationVariable Integer fromId, @DestinationVariable Integer rideId, String message) {

        PanicMessageDTO panicMessageDTO = new PanicMessageDTO(message,fromId, rideId);
        if (fromId != null && fromId != 0 ) {

            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/panic-chat/admin",
                    panicMessageDTO);
        }

        return panicMessageDTO;
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
