package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.exception.UnauthorizedException;
import com.uberTim12.ihor.model.communication.Panic;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.repository.communication.IPanicRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.communication.interfaces.IPanicService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PanicService extends JPAService<Panic> implements IPanicService {

    private final IPanicRepository panicRepository;

    private final IRideService rideService;

    private final IUserService userService;

    @Autowired
    public PanicService(IPanicRepository panicRepository, IRideService rideService, IUserService userService) {
        this.panicRepository = panicRepository;
        this.rideService = rideService;
        this.userService = userService;
    }

    @Override
    protected JpaRepository<Panic, Integer> getEntityRepository() {
        return panicRepository;
    }

    @Override
    public List<Panic> findAll(){
        List<Panic> panics = panicRepository.findAll();
        for (Panic panic : panics){
            Ride curRide = panic.getCurrentRide();
            Set<Passenger> passengers = new HashSet<>(rideService.findPassengersForRide(curRide.getId()));
            curRide.setPassengers(passengers);

            Set<Path> paths = new HashSet<>(rideService.findPathsForRide(curRide.getId()));
            curRide.setPaths(paths);
        }

        return panics;
    }

    @Override
    public Panic createForRide(Ride ride, String reason) throws UnauthorizedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Unauthorized!");
        }

        Panic panic = new Panic();

        String currentUserName = authentication.getName();
        User user = userService.findByEmail(currentUserName);
        panic.setUser(user);

        panic.setCurrentRide(ride);
        panic.setTime(LocalDateTime.now());
        panic.setReason(reason);

        return this.save(panic);
    }
}
