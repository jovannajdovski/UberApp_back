package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.dto.communication.FullReviewDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewsForRideDTO {
    private Integer rideId;
    private List<FullReviewDTO> reviews;

}
