package com.uberTim12.ihor.timer;

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
    Ride ride;
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
        this.ride=rideService.get(rideId);
        Location currentLocation=ride.getDriver().getVehicle().getCurrentLocation();
        this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"vehicle/current-location/"+ride.getId() , new LocationDTO(currentLocation));

    }

    public void finishRide(Integer rideId) {
        this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +"vehicle/current-location/"+rideId , new LocationDTO("finish",0.0,0.0));
        this.cancel();
    }
}
