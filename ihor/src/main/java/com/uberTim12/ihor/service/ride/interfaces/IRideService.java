package com.uberTim12.ihor.service.ride.interfaces;

import com.uberTim12.ihor.dto.ride.RideRequestDTO;
import com.uberTim12.ihor.dto.ride.RideResponseDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IRideService {
    Page<Ride> findFilteredRides(Integer driverId, Pageable pageable);

    Page<Ride> findFilteredRides(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    RideResponseDTO getEstimatedRoute(RideRequestDTO rideRequestDTO);

    Page<Ride> getRides(Integer userId, LocalDateTime start, LocalDateTime end, Pageable page);

    Ride save(Ride ride);
    Ride findById(Integer id);

    Ride findActiveByDriver(Driver driver);

    Ride findActiveByPassenger(Passenger passenger);

    List<Passenger> findPassengersForRide(Integer id);

    List<Path> findPathsForRide(Integer id);

    double getTimeOfNextRidesByDriverAtChoosedDay(Integer driverId, LocalDate now);
}
