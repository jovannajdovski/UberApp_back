package com.uberTim12.ihor.service.route.impl;

import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.route.IPathRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import com.uberTim12.ihor.service.route.interfaces.IPathService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PathService extends JPAService<Path> implements IPathService {
    private final IPathRepository pathRepository;

    public PathService(IPathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    @Override
    protected JpaRepository<Path, Integer> getEntityRepository() {
        return pathRepository;
    }
}
