package com.uberTim12.ihor.dto.communication;

import com.uberTim12.ihor.model.communication.MessageType;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SendingMessageDTO {

    private Integer receiverId;
    private String message;
    private MessageType type;
    private Integer rideId;
}

