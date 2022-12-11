package com.uberTim12.ihor.repository.communication;

import com.uberTim12.ihor.model.communication.Review;
import com.uberTim12.ihor.model.ride.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Integer> {

    Review findByRide(Ride ride);
    @Query("select r from Review r where r.ride.driver.vehicle.id=?1 and r.vehicleRate is not null ")
    List<Review> getReviewsForVehicle(Integer vehicleId);

    @Query("select r from Review r where r.ride.driver.id=?1 and r.driverRate is not null")
    List<Review> getReviewsForDriver(Integer driverId);

    @Query("select r from Review r where r.ride.id=?1")
    List<Review> getReviewsForRide(Integer rideId);
}
