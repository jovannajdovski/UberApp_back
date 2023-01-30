package com.uberTim12.ihor.dto.communication;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.ride.RideNoStatusDTO;
import com.uberTim12.ihor.dto.users.UserPanicDTO;
import com.uberTim12.ihor.model.communication.Panic;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PanicDTO {

    private Integer id;

    private UserPanicDTO user;

    private RideNoStatusDTO ride;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime time;

    private String reason;

    public PanicDTO(Panic panic){
        this.id = panic.getId();
        this.user = new UserPanicDTO(panic.getUser());
        this.ride = new RideNoStatusDTO(panic.getCurrentRide());
        this.time = panic.getTime();
        this.reason = panic.getReason();
    }
}
