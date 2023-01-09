package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.model.ride.Ride;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideResponseDTO {
    private Double estimatedTimeInMinutes;

    private Double estimatedCost;
    public RideResponseDTO(Ride ride)
    {
        this(ride.getEstimatedTime(),ride.getTotalPrice());
    }
}
