package com.uberTim12.ihor.repository.ride;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IRideRepository extends JpaRepository<Ride, Integer> {


    @Query("select r from Ride r join fetch r.passengers p join fetch r.paths l where r.driver =?1 and ?2 between r.startTime and r.endTime")
    public List<Ride> findActiveByDriver(Driver driver, LocalDateTime now);

    @Query("select r from Ride r join fetch r.passengers p join fetch r.paths l where ?1 member of r.passengers and ?2 between r.startTime and r.endTime")
    public List<Ride> findActiveByPassenger(Passenger passenger, LocalDateTime now);
}
