package com.uberTim12.ihor.util;

import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

@Component
public class SocketTimer extends TimerTask {
    SimpMessagingTemplate simpMessagingTemplate;
    IRideService rideService;

    int rideId;
    @Autowired
    public SocketTimer(IRideService rideService) {

        this.rideService=rideService;
    }
    public void setProperties(int rideId, SimpMessagingTemplate simpMessagingTemplate)
    {
        this.rideId=rideId;
        this.simpMessagingTemplate=simpMessagingTemplate;
    }

    @Override
    public void run() {
        Ride ride=rideService.get(rideId);
        Location currentLocation=ride.getDriver().getVehicle().getCurrentLocation();
        System.out.println(currentLocation.getLatitude());
        this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"vehicle/current-location/"+ride.getId() , new LocationDTO(currentLocation));

    }
}
