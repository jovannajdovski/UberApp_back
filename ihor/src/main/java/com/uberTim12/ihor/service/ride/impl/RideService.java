package com.uberTim12.ihor.service.ride.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRequestDTO;
import com.uberTim12.ihor.model.ride.RideResponseDTO;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RideService implements IRideService {
    @Autowired
    private IRideRepository IRideRepository;
    @Autowired
    private IDriverRepository IDriverRepository;
    @Autowired
    private IPassengerRepository IPassengerRepository;

    @Override
    public RideResponseDTO getEstimatedRoute(RideRequestDTO rideRequestDTO)
    {
        return new RideResponseDTO(LocalDateTime.of(2022,10,10,20,20), 450.0);
    }

    @Override
    public Page<Ride> getRides(Integer userId, LocalDateTime start, LocalDateTime end, Pageable page) {
        Optional<Driver> driver=IDriverRepository.findById(userId);
        if(driver.isPresent()) return IRideRepository.findAllInRangeForDriver(userId,start,end,page);

        Optional<Passenger> passenger=IPassengerRepository.findById(userId);
        return passenger.map(value -> IRideRepository.findAllInRangeForPassenger(value, start, end, page)).orElse(null);

    }
}
