package com.uberTim12.ihor.model.communication;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FullReviewDTO {
    private Integer id;
    private Double vehicleRate;
    private String vehicleComment;
    private Double driverRate;
    private String driverComment;
    private Passenger passenger;

    public FullReviewDTO(Review review)
    {
        this(review.getId(),review.getVehicleRate(),review.getVehicleComment(),review.getDriverRate(),review.getDriverComment(),review.getPassenger());
    }
}
