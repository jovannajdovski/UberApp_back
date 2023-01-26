package com.uberTim12.ihor.service.communication.interfaces;

import com.uberTim12.ihor.dto.communication.*;
import com.uberTim12.ihor.model.communication.Review;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.awt.*;
import java.util.List;

public interface IReviewService extends IJPAService<Review> {

    Review createVehicleReview(Integer passengerId, Integer rideId, Double rating, String comment) throws EntityNotFoundException;

    Review createDriverReview(Integer passengerId, Integer rideId, Double rating, String comment) throws EntityNotFoundException;

    List<Review> getReviewsForVehicle(Integer vehicleId);

    List<Review> getReviewsForDriver(Integer driverId);

    List<Review> getReviewsForRide(Integer rideId);
}
