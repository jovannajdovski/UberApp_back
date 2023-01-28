package com.uberTim12.ihor.web_socket;

import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import com.uberTim12.ihor.timer.SocketTimer;
import com.uberTim12.ihor.timer.WorkHoursTimer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.ArrayList;

@Controller
@Transactional
public class RideWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final IRideService rideService;
    private final IDriverService driverService;
    private final IUserService userService;
    private final SocketTimer socketTimer;
    private final WorkHoursTimer workHoursTimer;

    @Autowired
    public RideWebSocketController(SimpMessagingTemplate simpMessagingTemplate, IRideService rideService, IDriverService driverService, IUserService userService, SocketTimer socketTimer, WorkHoursTimer workHoursTimer) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.rideService = rideService;
        this.driverService = driverService;
        this.userService = userService;
        this.socketTimer = socketTimer;
        this.workHoursTimer = workHoursTimer;
    }

    @MessageMapping("vehicle/{rideId}/current-location")
    public void getVehicleCurrentLocation(@DestinationVariable Integer rideId) {

        try {
            Ride ride = rideService.get(rideId);

            socketTimer.setProperties(ride.getId(),this.simpMessagingTemplate);
            new Timer().scheduleAtFixedRate(socketTimer,0,2000);
        } catch (EntityNotFoundException  | IllegalStateException ignored) {
        }
    }

    @MessageMapping("{userId}/new-ride/{rideId}")
    public void getNewRide(@DestinationVariable Integer userId, @DestinationVariable Integer rideId) {
        Ride ride;
        try {
            userService.get(userId);
            ride=rideService.get(rideId);

            this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"new-ride/"+ride.getDriver().getId() , new RideFullDTO(ride));
            for(Passenger p:ride.getPassengers())
            {
                if(!Objects.equals(p.getId(), userId))
                    this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"new-ride/"+p.getId() , new RideFullDTO(ride));
            }

        } catch (EntityNotFoundException | IllegalStateException ignored) {}

    }

    @MessageMapping("{driverId}/work-hours")
    public void getRemainedWorkHours(@DestinationVariable Integer driverId){
        System.out.println("usao u soket");
        try {
            System.out.println(driverId);
            driverService.get(driverId);
            workHoursTimer.setProperties(driverId,this.simpMessagingTemplate);
            new Timer().scheduleAtFixedRate(workHoursTimer,0,60000);
        } catch (EntityNotFoundException | IllegalStateException ignored) {}

    }


    @MessageMapping("finish-ride/{rideId}")
    public void finishRide(@DestinationVariable Integer rideId) {
        Ride ride;
        try {
            ride=rideService.get(rideId);
            //this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"finished-ride/"+ride.getDriver().getId() , new RideFullDTO(ride));
            for(Passenger p:ride.getPassengers())
            {
                this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"finished-ride/"+p.getId() , new RideFullDTO(ride));
            }

        } catch (EntityNotFoundException  | IllegalStateException ignored) {}

    }



    private boolean passengerInPassengers(String passengerId, ArrayList<Passenger> passengers) {
        for (Passenger p : passengers)
            if (passengerId.equals(p.getId().toString()))
                return true;
        return false;
    }



}
