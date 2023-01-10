package com.uberTim12.ihor.service.ride.impl;


import com.uberTim12.ihor.exception.NoActiveRideException;
import com.uberTim12.ihor.exception.RideStatusException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.dto.ride.RideRequestDTO;
import com.uberTim12.ihor.dto.ride.RideResponseDTO;
import com.uberTim12.ihor.model.ride.RideRejection;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import java.util.List;
import java.util.Set;

@Service
public class RideService extends JPAService<Ride> implements IRideService {
    private final IRideRepository rideRepository;
    private final IDriverRepository driverRepository;
    private final IPassengerRepository passengerRepository;

    @Autowired
    public RideService(IRideRepository rideRepository, IDriverRepository driverRepository, IPassengerRepository passengerRepository) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.passengerRepository = passengerRepository;
    }

    @Override
    protected JpaRepository<Ride, Integer> getEntityRepository() {
        return rideRepository;
    }

    @Override
    public Page<Ride> findFilteredRides(Integer driverId, Pageable pageable) {
        return rideRepository.findByDriverId(driverId, pageable);
    }


    @Override
    public Page<Ride> findFilteredRides(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return rideRepository.findAllInRangeForDriver(driverId, from, to, pageable);
    }


    @Override
    public RideResponseDTO getEstimatedRoute(RideRequestDTO rideRequestDTO) {
        return new RideResponseDTO(20.0, 450.0);
    }


    @Override
    public Page<Ride> getRides(Integer userId, LocalDateTime start, LocalDateTime end, Pageable page) {
        Optional<Driver> driver = driverRepository.findById(userId);
        if (start == null) start = LocalDateTime.MIN;
        if (end == null) end = LocalDateTime.MAX;
        if (driver.isPresent())
            return rideRepository.findAllInRangeForDriver(userId, start, end, page);


        Optional<Passenger> passenger = passengerRepository.findById(userId);
        LocalDateTime finalStart = start;
        LocalDateTime finalEnd = end;
        return passenger.map(value -> rideRepository.findAllInRangeForPassenger(value, finalStart, finalEnd, page)).orElse(null);

    }

    @Override
    public Ride save(Ride ride) {
        return rideRepository.save(ride);
    }

//    @Override
//    public Ride get(Integer id){
//        Ride ride = rideRepository.findById(id).orElse(null);
//        if (ride==null){
//            return null;
//        } else {
//            Set<Passenger> passengers = new HashSet<>(findPassengersForRide(ride.getId()));
//            ride.setPassengers(passengers);
//            return ride;
//        }
//    }

    @Override
    public Ride findActiveByDriver(Driver driver) throws NoActiveRideException {
        List<Ride> rides = rideRepository.findActiveByDriver(driver, LocalDateTime.now());
        if (rides.isEmpty()) {
            throw new NoActiveRideException("Active ride does not exist!");
        } else {
            Ride ride = rides.get(0);
            Set<Passenger> passengers = new HashSet<>(findPassengersForRide(ride.getId()));
            ride.setPassengers(passengers);
            return ride;
        }
    }

    @Override
    public Ride findActiveByPassenger(Passenger passenger) throws NoActiveRideException {
        List<Ride> rides = rideRepository.findActiveByPassenger(passenger, LocalDateTime.now());
        if (rides.isEmpty()) {
            throw new NoActiveRideException("Active ride does not exist!");
        } else {
            Ride ride = rides.get(0);
            Set<Passenger> passengers = new HashSet<>(findPassengersForRide(ride.getId()));
            ride.setPassengers(passengers);
            return ride;
        }
    }

    @Override
    public List<Passenger> findPassengersForRide(Integer id) {
        return rideRepository.findPassengersForRide(id);
    }

    @Override
    public List<Path> findPathsForRide(Integer id) {
        return rideRepository.findPathsForRide(id);
    }

    @Override
    public double getTimeOfNextRidesByDriverAtChoosedDay(Integer driverId, LocalDate now) {
        Double res=rideRepository.sumEstimatedTimeOfNextRidesByDriverAtThatDay(driverId, now);
        if(res!=null) return res;
        return 0.0;
    }

    @Override
    public boolean hasIntersectionBetweenRides(LocalDateTime rideStart, LocalDateTime rideEnd, LocalDateTime newRideStart, LocalDateTime newRideEnd) {
        return rideEnd.isAfter(newRideStart) && newRideEnd.isAfter(rideStart);
    }

    @Override
    public Ride findCriticalRide(Set<Ride> rides, Ride newRide) {
        LocalDateTime rideEnd, newRideEnd=newRide.getStartTime().plusMinutes(newRide.getEstimatedTime().longValue());
        Ride criticalRide=null;
        LocalDateTime latestCriticalRideEnd=null;
        for(Ride ride: rides)
        {
            rideEnd=ride.getStartTime().plusMinutes(ride.getEstimatedTime().longValue());
            if(hasIntersectionBetweenRides(ride.getStartTime(),rideEnd,newRide.getStartTime(),newRideEnd)
                    && newRide.getStartTime().plusMinutes(30).isAfter(rideEnd))
            {
                if(latestCriticalRideEnd==null || rideEnd.isAfter(latestCriticalRideEnd))
                {
                    criticalRide=ride;
                    latestCriticalRideEnd=rideEnd;
                }
            }
        }
        return criticalRide;
    }

    public Ride cancel(Integer id) throws EntityNotFoundException, RideStatusException {
        Ride ride = this.get(id);

        if (ride.getRideStatus() != RideStatus.PENDING && ride.getRideStatus() != RideStatus.STARTED) {
            throw new RideStatusException("Cannot cancel a ride that is not in status PENDING or STARTED!");
        }

        ride.setRideStatus(RideStatus.CANCELED);
        return this.save(ride);
    }

    @Override
    public Ride start(Integer id) throws EntityNotFoundException, RideStatusException {
        Ride ride = this.get(id);

        if (ride.getRideStatus() != RideStatus.ACCEPTED) {
            throw new RideStatusException("Cannot start a ride that is not in status ACCEPTED!");
        }

        ride.setRideStatus(RideStatus.STARTED);
        return this.save(ride);
    }

    @Override
    public Ride accept(Integer id) throws EntityNotFoundException, RideStatusException {
        Ride ride = this.get(id);

        if (ride.getRideStatus() != RideStatus.PENDING) {
            throw new RideStatusException("Cannot accept a ride that is not in status PENDING!");
        }

        ride.setRideStatus(RideStatus.ACCEPTED);
        return this.save(ride);
    }

    @Override
    public Ride end(Integer id) throws EntityNotFoundException, RideStatusException {
        Ride ride = this.get(id);

        if (ride.getRideStatus() != RideStatus.STARTED) {
            throw new RideStatusException("Cannot end a ride that is not in status ACTIVE!");
        }

        ride.setRideStatus(RideStatus.FINISHED);
        return this.save(ride);
    }

    @Override
    public Ride reject(Integer id, String reason) throws EntityNotFoundException, RideStatusException {
        Ride ride = this.get(id);

        if (ride.getRideStatus() != RideStatus.PENDING) {
            throw new RideStatusException("Cannot cancel a ride that is not in status PENDING!");
        }

        if (ride.getRideRejection() == null) {
            ride.setRideRejection(new RideRejection());
        }
        ride.getRideRejection().setReason(reason);
        ride.getRideRejection().setTime(LocalDateTime.now());

        ride.setRideStatus(RideStatus.REJECTED);
        return this.save(ride);
    }
}
