package com.uberTim12.ihor.repository.communication;

import com.uberTim12.ihor.model.communication.FullReviewDTO;
import com.uberTim12.ihor.model.communication.Review;
import com.uberTim12.ihor.model.communication.ReviewDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Integer> {

    @Query("select r from Review r where r.ride.driver.vehicle.id=?1")
    List<Review> getReviewsForVehicle(Integer vehicleId);

    @Query("select r from Review r where r.ride.driver.id=?1")
    List<Review> getReviewsForDriver(Integer driverId);

    @Query("select r from Review r where r.ride.id=?1")
    List<Review> getReviewsForRide(Integer rideId);
}
