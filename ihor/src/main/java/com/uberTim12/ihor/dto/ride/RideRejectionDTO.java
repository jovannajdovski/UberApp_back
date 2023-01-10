package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.model.ride.RideRejection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideRejectionDTO {
    private String reason;

    private LocalDateTime timeOfRejection;

    public RideRejectionDTO(RideRejection rideRejection){
        this(rideRejection.getReason(), rideRejection.getTime());
    }
}
