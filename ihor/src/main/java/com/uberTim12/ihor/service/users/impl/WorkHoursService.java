package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.exception.ShiftAlreadyStartedException;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.repository.users.IWorkHoursRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Service
public class WorkHoursService extends JPAService<WorkHours> implements IWorkHoursService {

    private final IWorkHoursRepository workHoursRepository;
    private final IDriverService driverService;
    private final IVehicleService vehicleService;

    @Autowired
    WorkHoursService(IWorkHoursRepository workHoursRepository, IDriverService driverService, IVehicleService vehicleService) {
        this.workHoursRepository = workHoursRepository;
        this.driverService = driverService;
        this.vehicleService = vehicleService;
    }

    @Override
    protected JpaRepository<WorkHours, Integer> getEntityRepository() {
        return workHoursRepository;
    }

    @Override
    public WorkHours endShift(Integer workHoursId, LocalDateTime endTime) throws EntityNotFoundException {
        WorkHours workHours = get(workHoursId);
        workHours.setEndTime(endTime);
        return save(workHours);
    }

    @Override
    public WorkHours startShift(Integer driverId, WorkHours workHours) throws EntityNotFoundException,
            EntityPropertyIsNullException, ShiftAlreadyStartedException {
        Driver driver = driverService.get(driverId);
        vehicleService.getVehicleOf(driverId);
        shiftAlreadyStarted(driverId, workHours.getStartTime());

        workHours.setDriver(driver);
        return save(workHours);
    }

    @Override
    public void shiftAlreadyStarted(Integer driverId, LocalDateTime date) throws ShiftAlreadyStartedException {
        Set<WorkHours> workHours = workHoursRepository.findByDriverIdAndDateRange(driverId,
                date.with(LocalTime.MIN), date.with(LocalTime.MAX));

        if (!workHours.isEmpty())
            throw new ShiftAlreadyStartedException("Shift already ongoing!");
    }

    @Override
    public Page<WorkHours> findFilteredWorkHours(Integer driverId, Pageable pageable) {
        return workHoursRepository.findByDriverId(driverId, pageable);
    }

    @Override
    public Page<WorkHours> findFilteredWorkHours(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return workHoursRepository.findByDriverIdAndDateRange(driverId, from, to, pageable);
    }
}
