package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.service.users.interfaces.IUserActivationService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import com.uberTim12.ihor.util.ImageConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PassengerService extends JPAService<Passenger> implements IPassengerService {
    private final IPassengerRepository passengerRepository;
    private final IRideRepository rideRepository;
    private final IUserService userService;
    private final IUserActivationService userActivationService;

    @Autowired
    public PassengerService(IPassengerRepository passengerRepository, IRideRepository rideRepository, IUserService userService, IUserActivationService userActivationService) {
        this.passengerRepository = passengerRepository;
        this.rideRepository = rideRepository;
        this.userService = userService;
        this.userActivationService = userActivationService;
    }

    @Override
    protected JpaRepository<Passenger, Integer> getEntityRepository() {
        return passengerRepository;
    }

    @Override
    public Passenger register(Passenger passenger) throws EmailAlreadyExistsException {
        userService.emailTaken(passenger.getEmail());
        passenger.setActive(false);
        passenger = save(passenger);
        userActivationService.create(passenger);
        return passenger;
    }

    @Override
    public Passenger update(Integer passengerId, String name, String surname, String profilePicture,
                            String telephoneNumber, String email, String address, String password)
            throws EntityNotFoundException {
        Passenger passenger = get(passengerId);
        passenger.setName(name);
        passenger.setSurname(surname);
        passenger.setProfilePicture(ImageConverter.decodeToImage(profilePicture));
        passenger.setTelephoneNumber(telephoneNumber);
        passenger.setEmail(email);
        passenger.setAddress(address);
        if (password != null)
            passenger.setPassword(password);
        return save(passenger);
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
    public Optional<Passenger> findByIdWithFavorites(Integer id) {
        return passengerRepository.findById(id);
    }

    @Override
    public Passenger findByEmailWithFavorites(String email) {
        return passengerRepository.findByEmailWithFavorites(email);
    }
}
