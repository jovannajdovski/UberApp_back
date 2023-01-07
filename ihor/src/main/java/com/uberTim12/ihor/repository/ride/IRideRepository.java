package com.uberTim12.ihor.repository.ride;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface IRideRepository extends JpaRepository<Ride, Integer> {
    @Query("select r from Ride r where r.driver.id =?1")
    Page<Ride> findByDriverId(Integer driverId, Pageable pageable);

    @Query("select r from Ride r where r.driver.id =?1 and r.startTime between ?2 and ?3")
    public Page<Ride> findAllInRangeForDriver(Integer id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select r from Ride r where ?1 member of r.passengers and r.startTime between ?2 and ?3")
    public Page<Ride> findAllInRangeForPassenger(Passenger passenger, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select r from Ride r join r.passengers p where ?1 member of r.passengers")
    public Page<Ride> findAllForPassenger(Passenger passenger, Pageable pageable);

    @Query("select r from Ride r join fetch r.passengers p join fetch r.paths l where r.driver =?1 and ?2 between r.startTime and r.endTime")
    public List<Ride> findActiveByDriver(Driver driver, LocalDateTime now);

    @Query("select r from Ride r join fetch r.passengers p join fetch r.paths l where ?1 member of r.passengers and ?2 between r.startTime and r.endTime")
    public List<Ride> findActiveByPassenger(Passenger passenger, LocalDateTime now);

    @Query("select r.passengers from Ride as r join r.passengers as p where r.id =?1")
    public List<Passenger> findPassengersForRide(Integer id);

    @Query("select r.paths from Ride as r join r.paths as p where r.id =?1")
    public List<Path> findPathsForRide(Integer id);

    @Query("select sum(r.estimatedTime) from Ride r where r.driver.id=?1 and (r.rideStatus=?2 or r.rideStatus=?3) and cast(r.startTime as localdate)=?4")
    public double sumByDriverIdAndRideStatusAndStartTimeDate(Integer driverId, RideStatus accepted, RideStatus active, LocalDate date);
}
