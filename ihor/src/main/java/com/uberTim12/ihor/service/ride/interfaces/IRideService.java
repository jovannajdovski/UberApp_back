package com.uberTim12.ihor.service.ride.interfaces;

import com.uberTim12.ihor.dto.ride.RideResponseDTO;
import com.uberTim12.ihor.exception.NoAcceptedRideException;
import com.uberTim12.ihor.exception.NoActiveRideException;
import com.uberTim12.ihor.exception.RideStatusException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface IRideService extends IJPAService<Ride> {
    Page<Ride> findFilteredRides(Integer driverId, Pageable pageable);

    Page<Ride> findFilteredRides(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Ride> findFilteredFinishedRidesDriver(Integer driverId, Pageable pageable);

    Page<Ride> findFilteredFinishedRidesAdmin(Pageable pageable);

    Page<Ride> findFilteredFinishedRidesPassenger(Integer passengerId, Pageable pageable);

    Page<Ride> findFilteredRidesForUser(Integer userId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Ride> findFilteredRidesForUser(Integer userId, Pageable pageable);

    RideResponseDTO getEstimatedRoute(Ride ride);

    Page<Ride> getRides(Integer userId, LocalDateTime start, LocalDateTime end, Pageable page);

    Ride findActiveByDriver(Driver driver) throws NoActiveRideException;

    List<Ride> findAcceptedByDriver(Driver driver) throws NoActiveRideException;

    Ride findActiveByPassenger(Passenger passenger) throws NoActiveRideException;

    List<Passenger> findPassengersForRide(Integer id);

    List<Path> findPathsForRide(Integer id);

    double getTimeOfNextRidesByDriverAtChoosedDay(Integer driverId, LocalDate now);

    boolean hasIntersectionBetweenRides(LocalDateTime rideStart, LocalDateTime rideEnd, LocalDateTime newRideStart, LocalDateTime newRideEnd);

    Ride findCriticalRide(Set<Ride> rides, Ride newRide);
    
    Ride cancel(Integer id) throws EntityNotFoundException, RideStatusException;

    Ride start(Integer id, Integer driverId) throws EntityNotFoundException, RideStatusException;

    Ride accept(Integer id) throws EntityNotFoundException, RideStatusException;

    Ride end(Integer id) throws EntityNotFoundException, RideStatusException;

    Ride reject(Integer id, String reason) throws EntityNotFoundException, RideStatusException;

    List<Ride> findPendingRides(Integer driverId);

    Ride findNextRide(Integer driverId) throws NoAcceptedRideException;

    List<Ride> findRidesWithStatusForDriver(Integer id, RideStatus status, LocalDateTime from, LocalDateTime to);

    List<Ride> findRidesWithStatusForPassenger(Integer id, RideStatus status, LocalDateTime from, LocalDateTime to);

    List<Ride> findAcceptedRides(Integer id, LocalDateTime from, LocalDateTime to);

    List<Ride> findAllRidesWithStatusInTimeRange(RideStatus status, LocalDateTime from, LocalDateTime to);
}
