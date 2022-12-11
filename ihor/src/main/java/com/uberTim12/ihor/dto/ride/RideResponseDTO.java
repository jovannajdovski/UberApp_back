package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.model.ride.Ride;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RideResponseDTO {
    private Double estimatedTimeInMinutes;

    private Double estimatedCost;
    public RideResponseDTO(Ride ride)
    {
        this(ride.getEstimatedTime(),ride.getTotalPrice());
    }
}