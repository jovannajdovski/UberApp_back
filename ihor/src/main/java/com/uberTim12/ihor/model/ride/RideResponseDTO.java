package com.uberTim12.ihor.model.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RideResponseDTO {
    private Double estimatedTime;

    private Double estimatedPrice;
    public RideResponseDTO(Ride ride)
    {
        this(ride.getEstimatedTime(),ride.getTotalPrice());
    }
}
