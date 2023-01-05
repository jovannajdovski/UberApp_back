package com.uberTim12.ihor.service.route.impl;


import com.uberTim12.ihor.model.route.Location;

import com.uberTim12.ihor.repository.route.ILocationRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class LocationService extends JPAService<Location> implements ILocationService {
    private final ILocationRepository locationRepository;

    @Autowired
    LocationService(ILocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    protected JpaRepository<Location, Integer> getEntityRepository() {
        return locationRepository;
    }
}
