package com.uberTim12.ihor.dto.communication;

import com.uberTim12.ihor.dto.ride.RideDTO;
import com.uberTim12.ihor.dto.users.UserPanicDTO;
import com.uberTim12.ihor.model.communication.Panic;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PanicDTO {

    private Integer id;

    private UserPanicDTO user;

    private RideDTO ride;

    private LocalDateTime time;

    private String reason;

    public PanicDTO(Panic panic){
        this.id = panic.getId();
        this.user = new UserPanicDTO(panic.getUser());
        this.ride = new RideDTO(panic.getCurrentRide());
        this.time = panic.getTime();
        this.reason = panic.getReason();
    }
}
