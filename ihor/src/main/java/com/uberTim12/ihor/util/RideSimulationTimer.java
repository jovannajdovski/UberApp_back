package com.uberTim12.ihor.util;

import com.uberTim12.ihor.dto.route.RouteStep;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TimerTask;

public class RideSimulationTimer extends TimerTask {
    int i=0;
    List<RouteStep> steps;
    Integer vehicleId;
    IVehicleService vehicleService;

    public RideSimulationTimer(Integer vehicleId, IVehicleService vehicleService, List<RouteStep> steps) {
        this.steps = steps;
        this.vehicleId = vehicleId;
        this.vehicleService=vehicleService;
    }

    @Override
    public void run() {
        vehicleService.changeVehicleLocation(vehicleId,new Location("Adresa",steps.get(i).getLatitude(), steps.get(i).getLongitude()));
        i++;
        if(i==steps.size())
            this.cancel();
    }
}
