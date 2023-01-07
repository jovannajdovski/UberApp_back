package com.uberTim12.ihor.service.route.interfaces;


import com.uberTim12.ihor.model.route.Location;

public interface ILocationService {

    Location save(Location location);

    Double calculateDistance(Location location1, Location location2);
}
