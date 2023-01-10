package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.model.communication.*;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.communication.IReviewRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.security.AuthUtil;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.communication.interfaces.IReviewService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService extends JPAService<Review> implements IReviewService {
    private final IReviewRepository reviewRepository;
    private final IRideService rideService;
    private final IPassengerService passengerService

    @Autowired
    public ReviewService(IReviewRepository reviewRepository, IRideService rideService, IPassengerService passengerService) {
        this.reviewRepository = reviewRepository;
        this.rideService = rideService;
        this.passengerService = passengerService;
    }

    @Override
    protected JpaRepository<Review, Integer> getEntityRepository() {
        return reviewRepository;
    }

    @Override
    public Review createVehicleReview(Integer rideId, Double rating, String comment) throws EntityNotFoundException {
        Ride ride = rideService.get(rideId);
        Passenger passenger = passengerService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        Review review=reviewRepository.findByRide(ride);
        if(review!=null) {
            review.setVehicleRate(rating);
            review.setVehicleComment(comment);
        }
        else{
            review = new Review(rating, comment, null, null, passenger, ride);
        }

        return save(review);
    }

    @Override
    public Review createDriverReview(Integer rideId, Double rating, String comment) throws EntityNotFoundException {
        Ride ride = rideService.get(rideId);
        Passenger passenger = passengerService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        Review review=reviewRepository.findByRide(ride);
        if(review!=null) {
            review.setDriverRate(rating);
            review.setDriverComment(comment);
        }
        else{
            review = new Review(null, null, rating, comment, passenger, ride);
        }

        return save(review);
    }

    @Override
    public List<Review> getReviewsForVehicle(Integer vehicleId) {
        return reviewRepository.getReviewsForVehicle(vehicleId);
    }

    @Override
    public List<Review> getReviewsForDriver(Integer driverId) {
        return reviewRepository.getReviewsForDriver(driverId);
    }

    @Override
    public List<Review> getReviewsForRide(Integer rideId) {
        return reviewRepository.getReviewsForRide(rideId);
    }
}
