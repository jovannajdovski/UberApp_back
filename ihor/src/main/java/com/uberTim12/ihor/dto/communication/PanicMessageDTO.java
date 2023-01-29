package com.uberTim12.ihor.dto.communication;

import com.uberTim12.ihor.dto.ride.RideNoStatusDTO;
import com.uberTim12.ihor.dto.users.UserPanicDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PanicMessageDTO {
    private String message;
    private UserPanicDTO user;
    private RideNoStatusDTO ride;
}
