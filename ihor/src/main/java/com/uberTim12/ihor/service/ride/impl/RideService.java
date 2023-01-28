package com.uberTim12.ihor.service.ride.impl;


import com.uberTim12.ihor.exception.NoAcceptedRideException;
import com.uberTim12.ihor.exception.NoActiveRideException;
import com.uberTim12.ihor.exception.RideStatusException;
import com.uberTim12.ihor.model.ride.Ride;
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
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import jakarta.persistence.EntityNotFoundException;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final ILocationService locationService;

    @Autowired
    public RideService(IRideRepository rideRepository, IDriverRepository driverRepository, IPassengerRepository passengerRepository, ILocationService locationService) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.passengerRepository = passengerRepository;
        this.locationService = locationService;
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
    public Page<Ride> findFilteredFinishedRidesDriver(Integer driverId, Pageable pageable) {
        return rideRepository.findAllFinishedForDriver(driverId, RideStatus.FINISHED, pageable);
    }

    @Override
    public Page<Ride> findFilteredFinishedRidesAdmin(Pageable pageable) {
        return rideRepository.findAllFinishedForAdmin(RideStatus.FINISHED, pageable);
    }

    @Override
    public Page<Ride> findFilteredFinishedRidesPassenger(Integer passengerId, Pageable pageable) {
        Passenger passenger = passengerRepository.findById(passengerId).orElse(null);
        if (passenger==null){
            throw new EntityNotFoundException("Passenger does not exists!");
        }
        return rideRepository.findAllFinishedForPassenger(passenger, RideStatus.FINISHED, pageable);
    }

    @Override
    public Page<Ride> findFilteredRidesForUser(Integer userId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return rideRepository.findAllInRangeForUser(userId, from, to, pageable);
    }

    @Override
    public Page<Ride> findFilteredRidesForUser(Integer userId, Pageable pageable) {
        return rideRepository.findAllInRangeForUser(userId, pageable);
    }

    @Override
    public RideResponseDTO getEstimatedRoute(Ride ride) {
        Double distance, estimatedTime;
        try{
            distance=locationService.calculateDistance(ride.getPaths().iterator().next().getStartPoint(), ride.getPaths().iterator().next().getEndPoint());
        }
        catch (ParseException | IOException e)
        {
            distance=Double.MAX_VALUE;
        }
        try{
            estimatedTime=locationService.calculateEstimatedTime(ride.getPaths().iterator().next().getStartPoint(), ride.getPaths().iterator().next().getEndPoint());
        }
        catch (ParseException | IOException e)
        {
            estimatedTime=Double.MAX_VALUE;
        }
//        return new RideResponseDTO(estimatedTime, ride.getVehicleType().getPricePerKM()+distance*120);
        return new RideResponseDTO(estimatedTime, 500+distance*120);

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

    @Override
    public Ride findActiveByDriver(Driver driver) throws NoActiveRideException {
        List<Ride> rides = rideRepository.findActiveByDriver(driver, RideStatus.STARTED);
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
    public List<Ride> findAcceptedByDriver(Driver driver) throws NoActiveRideException {
        List<Ride> rides = rideRepository.findActiveByDriver(driver, RideStatus.ACCEPTED);
        if (rides.isEmpty()) {
            throw new NoActiveRideException("Accepted ride does not exist!");
        } else {
            for (Ride ride: rides){
                Set<Passenger> passengers = new HashSet<>(findPassengersForRide(ride.getId()));
                ride.setPassengers(passengers);
            }

            return rides;
        }
    }

    @Override
    public Ride findActiveByPassenger(Passenger passenger) throws NoActiveRideException {
        List<Ride> rides = rideRepository.findActiveByPassenger(passenger, RideStatus.STARTED);
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
        ride.setStartTime(null);
        ride.setRideStatus(RideStatus.CANCELED);
        return this.save(ride);
    }

    @Override
    public Ride start(Integer id, Integer driverId) throws EntityNotFoundException, RideStatusException {
        Ride ride = this.get(id);

        if (ride.getRideStatus() != RideStatus.ACCEPTED) {
            throw new RideStatusException("Cannot start a ride that is not in status ACCEPTED!");
        }

        Driver driver = driverRepository.findById(driverId).orElse(null);
        if (driver==null){
            throw new EntityNotFoundException("Driver does not exists!");
        }
        List<Ride> rides = rideRepository.findActiveByDriver(driver, RideStatus.STARTED);
        if (rides.size()>0){
            throw new RideStatusException("Cannot start a ride if you already have STARTED ride!");
        }

        ride.setRideStatus(RideStatus.STARTED);
        ride.setStartTime(LocalDateTime.now());
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
        ride.setEndTime(LocalDateTime.now());
        return this.save(ride);
    }

    @Override
    public Ride reject(Integer id, String reason) throws EntityNotFoundException, RideStatusException {
        Ride ride = this.get(id);

        if (ride.getRideStatus() != RideStatus.PENDING && ride.getRideStatus()!=RideStatus.ACCEPTED) {
            throw new RideStatusException("Cannot cancel a ride that is not in status PENDING or ACCEPTED!");
        }

        if (ride.getRideRejection() == null) {
            ride.setRideRejection(new RideRejection());
        }
        ride.getRideRejection().setReason(reason);
        ride.getRideRejection().setTime(LocalDateTime.now());
        ride.getRideRejection().setRide(ride);
        ride.getRideRejection().setUser(ride.getDriver());
        ride.setStartTime(null);
        ride.setRideStatus(RideStatus.REJECTED);
        return this.save(ride);
    }

    @Override
    public List<Ride> findPendingRides(Integer driverId) {
        return rideRepository.findAllByDriverIdAndRideStatusOrderByStartTime(driverId, RideStatus.PENDING);
    }
    @Override
    public Ride findNextRide(Integer driverId) throws NoAcceptedRideException {
        List<Ride> acceptedRides=rideRepository.findAllByDriverIdAndRideStatusOrderByStartTime(driverId, RideStatus.ACCEPTED);
        if(acceptedRides.size()==0)
            throw new NoAcceptedRideException("No accepted rides");
        return acceptedRides.get(0);
    }

    @Override
    public List<Ride> findRidesWithStatusForPassenger(Integer id, RideStatus status, LocalDateTime from, LocalDateTime to) {
        return rideRepository.findAllByPassengerIdAndRideStatusInTimeRange(id, status, from, to);
    }

    @Override
    public List<Ride> findRidesWithStatusForDriver(Integer id, RideStatus status, LocalDateTime from, LocalDateTime to) {
        return rideRepository.findAllByDriverIdAndRideStatusInTimeRange(id, status, from, to);
    }

    @Override
    public List<Ride> findAcceptedRides(Integer id, LocalDateTime from, LocalDateTime to) {
        return rideRepository.findAllAccepted(id, from, to);
    }
}
