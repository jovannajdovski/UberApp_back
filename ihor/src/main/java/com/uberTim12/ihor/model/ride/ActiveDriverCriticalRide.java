package com.uberTim12.ihor.model.ride;

import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActiveDriverCriticalRide {
    Driver driver;
    Location location;
    Ride criticalRide;

    public ActiveDriverCriticalRide(ActiveDriver activeDriver, Ride criticalRide)
    {
        this.driver=activeDriver.getDriver();
        this.location=activeDriver.getLocation();
        this.criticalRide=criticalRide;
    }
}

