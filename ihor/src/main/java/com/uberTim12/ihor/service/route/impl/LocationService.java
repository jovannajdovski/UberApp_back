package com.uberTim12.ihor.service.route.impl;

import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.repository.route.ILocationRepository;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService implements ILocationService {

    private ILocationRepository locationRepository;
    @Autowired
    LocationService(ILocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location save(Location location) {
        return locationRepository.save(location);
    }

}
