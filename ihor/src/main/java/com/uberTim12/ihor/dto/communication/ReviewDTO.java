package com.uberTim12.ihor.dto.communication;

import com.uberTim12.ihor.dto.users.PassengerIdentificatorsDTO;
import com.uberTim12.ihor.model.communication.Review;
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
public class ReviewDTO {
    private Integer id;

    private Double rating;

    private String comment;

    private PassengerIdentificatorsDTO passengerIdentificatorsDTO;
    public ReviewDTO(Review review, boolean isVehicleReview)
    {
        this(review.getId(),(isVehicleReview)?review.getVehicleRate(): review.getDriverRate(), (isVehicleReview)?review.getVehicleComment():review.getDriverComment(),new PassengerIdentificatorsDTO(review.getPassenger()));
    }
}
