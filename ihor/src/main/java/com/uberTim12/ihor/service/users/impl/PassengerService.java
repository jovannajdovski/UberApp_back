package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.repository.users.IAuthorityRepository;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.service.users.interfaces.IUserActivationService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import com.uberTim12.ihor.util.ImageConverter;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PassengerService extends JPAService<Passenger> implements IPassengerService {
    private final IPassengerRepository passengerRepository;
    private final IRideRepository rideRepository;
    private final IUserService userService;
    private final IUserActivationService userActivationService;
    private final IRideService rideService;
    private final PasswordEncoder passwordEncoder;
    private final IAuthorityRepository authorityRepository;

    @Autowired
    public PassengerService(IPassengerRepository passengerRepository, IRideRepository rideRepository, IUserService userService, IUserActivationService userActivationService, IRideService rideService, PasswordEncoder passwordEncoder, IAuthorityRepository authorityRepository) {
        this.passengerRepository = passengerRepository;
        this.rideRepository = rideRepository;
        this.userService = userService;
        this.userActivationService = userActivationService;
        this.rideService = rideService;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Override
    protected JpaRepository<Passenger, Integer> getEntityRepository() {
        return passengerRepository;
    }

    @Override
    public Passenger register(Passenger passenger) throws EmailAlreadyExistsException, MessagingException, UnsupportedEncodingException {
        userService.emailTaken(passenger.getEmail());
        passenger.setActive(false);
        passenger.setAuthority(authorityRepository.findById(3).get());
        passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
        passenger = save(passenger);

        userActivationService.create(passenger);
        return passenger;
    }

    @Override
    public Passenger update(Integer passengerId, String name, String surname, String profilePicture,
                            String telephoneNumber, String email, String address)
            throws EntityNotFoundException {
        Passenger passenger = get(passengerId);
        passenger.setName(name);
        passenger.setSurname(surname);
        passenger.setProfilePicture(ImageConverter.decodeToImage(profilePicture));
        passenger.setTelephoneNumber(telephoneNumber);
        passenger.setEmail(email);
        passenger.setAddress(address);
        return save(passenger);
    }

    @Override
    public Passenger findByEmail(String email) {
        return passengerRepository.findByEmail(email);
    }

    @Override
    public Page<Ride> findAllById(Integer passengerId, LocalDateTime start, LocalDateTime end, Pageable page){
        Optional<Passenger> passenger=passengerRepository.findById(passengerId);
        return passenger.map(value -> rideRepository.findAllInRangeForPassenger(value, start, end, page)).orElse(null);
    }

    @Override
    public Page<Ride> findAllById(Passenger passenger, Pageable page){
        return rideRepository.findAllForPassenger(passenger, page);
    }

    @Override
    public Passenger findByIdWithRides(Integer id) {
        return passengerRepository.findByIdWithRides(id);
    }

    @Override
    public Passenger findByIdWithFavorites(Integer id) {
        return passengerRepository.findByIdWithFavorites(id);
    }

    @Override
    public Passenger findByEmailWithFavorites(String email) {
        return passengerRepository.findByEmailWithFavorites(email);
    }

    @Override
    public boolean isPassengersFree(Ride newRide) {
        LocalDateTime rideStart, rideEnd;
        LocalDateTime newRideStart=newRide.getStartTime(), newRideEnd=newRideStart.plusMinutes(newRide.getEstimatedTime().longValue());
        for(Passenger passenger: newRide.getPassengers())
        {
            for(Ride ride: passenger.getRides())
            {
                rideStart=ride.getStartTime();
                rideEnd=rideStart.plusMinutes(ride.getEstimatedTime().longValue());
                if(rideService.hasIntersectionBetweenRides(rideStart, rideEnd, newRideStart,newRideEnd) &&
                        (ride.getRideStatus()== RideStatus.ACCEPTED || ride.getRideStatus()==RideStatus.STARTED))
                    return false;
            }
        }
        return true;
    }
}
