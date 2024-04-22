package com.uberTim12.ihor.dto.communication;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uberTim12.ihor.model.communication.Message;
import com.uberTim12.ihor.model.communication.MessageType;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageDTO {

    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timeOfSending;
    private Integer senderId;
    private Integer receiverId;
    private String message;
    private MessageType type;
    private Integer rideId;

    public MessageDTO(Message message)
    {
        this.id=message.getId();
        this.timeOfSending=message.getSendTime();
        this.senderId=(message.getSender()==null)? 0: message.getSender().getId();
        this.receiverId=(message.getReceiver()==null)?0:message.getReceiver().getId();
        this.message=message.getContent();
        this.type=message.getType();
        this.rideId=(message.getRide()==null)?0:message.getRide().getId();
    }
}
