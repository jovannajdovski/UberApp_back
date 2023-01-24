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
import java.util.List;

@Repository
public interface IRideRepository extends JpaRepository<Ride, Integer> {
    @Query("select r from Ride r where r.driver.id =?1")
    Page<Ride> findByDriverId(Integer driverId, Pageable pageable);

    List<Ride> findAllByDriverIdAndRideStatus(Integer driver_id, RideStatus rideStatus);

    @Query("select r from Ride r where r.driver.id =?1 and r.rideStatus = ?2 and r.startTime between ?3 and ?4")
    List<Ride> findAllByDriverIdAndRideStatusInTimeRange(Integer driver_id, RideStatus rideStatus, LocalDateTime from, LocalDateTime to);

    @Query("select r from Ride r where r.driver.id =?1 and r.rideStatus != 0 and r.rideStatus != 3 " +
            "and r.rideStatus != 5 and r.startTime between ?2 and ?3")
    List<Ride> findAllAccepted(Integer driver_id, LocalDateTime from, LocalDateTime to);

    @Query("select r from Ride r where r.driver.id =?1 and r.startTime between ?2 and ?3")
    Page<Ride> findAllInRangeForDriver(Integer id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select r from Ride r join Passenger p on p.id = ?1 where r.startTime between ?2 and ?3") //or r.driver.id=?1 and
    Page<Ride> findAllInRangeForUser(Integer id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select r from Ride r join Passenger p on p.id = ?1")// or r.driver.id=?1
    Page<Ride> findAllInRangeForUser(Integer id, Pageable pageable);

    @Query("select r from Ride r where ?1 member of r.passengers and r.startTime between ?2 and ?3")
    Page<Ride> findAllInRangeForPassenger(Passenger passenger, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select r from Ride r join r.passengers p where ?1 member of r.passengers")
    Page<Ride> findAllForPassenger(Passenger passenger, Pageable pageable);

    @Query("select r from Ride r join fetch r.passengers p join fetch r.paths l where r.driver =?1 and r.rideStatus = ?2")
    List<Ride> findActiveByDriver(Driver driver, RideStatus rideStatus);

    @Query("select r from Ride r join fetch r.passengers p join fetch r.paths l where ?1 member of r.passengers and r.rideStatus = ?2")
    List<Ride> findActiveByPassenger(Passenger passenger, RideStatus rideStatus);

    @Query("select r.passengers from Ride as r join r.passengers as p where r.id =?1")
    List<Passenger> findPassengersForRide(Integer id);

    @Query("select r.paths from Ride as r join r.paths as p where r.id =?1")
    List<Path> findPathsForRide(Integer id);

    @Query("select sum(r.estimatedTime) from Ride r where r.driver.id=?1  and (r.rideStatus=2 or r.rideStatus=1) and cast(r.startTime as localdate)=?2")
    Double sumEstimatedTimeOfNextRidesByDriverAtThatDay(Integer driverId, LocalDate date);

    /*@Query("select sum(r.estimatedTime) from Ride r where r.driver.id=?1 and r.endTime IS NULL")
    public double sumEstimatedTimeOfNextRidesByDriverAtThatDay(Integer driverId);*/

}
