package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.exception.ShiftAlreadyStartedException;
import com.uberTim12.ihor.exception.ShiftIsNotOngoingException;
import com.uberTim12.ihor.exception.WorkTimeExceededException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.model.users.WorkHoursComp;
import com.uberTim12.ihor.repository.users.IWorkHoursRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;

@Service
public class WorkHoursService extends JPAService<WorkHours> implements IWorkHoursService {
    private final IWorkHoursRepository workHoursRepository;
    private final IDriverService driverService;
    private final IVehicleService vehicleService;
    private final IRideService rideService;

    @Autowired
    WorkHoursService(IWorkHoursRepository workHoursRepository, IDriverService driverService,
                     IVehicleService vehicleService, IRideService rideService) {
        this.workHoursRepository = workHoursRepository;
        this.driverService = driverService;
        this.vehicleService = vehicleService;
        this.rideService = rideService;
    }

    @Override
    protected JpaRepository<WorkHours, Integer> getEntityRepository() {
        return workHoursRepository;
    }

    @Override
    public boolean isDriverAvailable(Driver driver, Ride ride) {
        return getWorkingMinutesByDriverAtChosenDay(driver.getId(), LocalDate.now())
                + rideService.getTimeOfNextRidesByDriverAtChoosedDay(driver.getId(),LocalDate.now())
                + ride.getEstimatedTime() <= 8 * 60;
    }

    @Override
    public WorkHours endShift(Integer workHoursId, LocalDateTime endTime) throws EntityNotFoundException,
            EntityPropertyIsNullException, ShiftIsNotOngoingException {
        WorkHours workHours = get(workHoursId);

        Driver driver=workHours.getDriver();
        vehicleService.getVehicleOf(driver.getId());
        if(workHours.getEndTime()!=null)
            throw new ShiftIsNotOngoingException("No shift is ongoing!");
        workHours.setEndTime(endTime);
        return save(workHours);
    }

    @Override
    public WorkHours startShift(Integer driverId, WorkHours workHours) throws EntityNotFoundException,
            EntityPropertyIsNullException, ShiftAlreadyStartedException,
            WorkTimeExceededException {
        Driver driver = driverService.get(driverId);
        vehicleService.getVehicleOf(driverId);
        canShiftStart(driverId, workHours.getStartTime());

        workHours.setDriver(driver);
        return save(workHours);
    }

    private void canShiftStart(Integer driverId, LocalDateTime date)
            throws ShiftAlreadyStartedException, WorkTimeExceededException {
        Set<WorkHours> workHours = workHoursRepository.findByDriverIdAndDateRange(driverId,
                date.with(LocalTime.MIN), date.with(LocalTime.MAX));
        shiftOngoing(workHours);
        workTimeExceeded(workHours);
    }

    private void shiftOngoing(Set<WorkHours> workHours) throws ShiftAlreadyStartedException {
        TreeSet<WorkHours> workHoursSorted = new TreeSet<>(new WorkHoursComp());
        workHoursSorted.addAll(workHours);

        if (!workHoursSorted.isEmpty() && workHoursSorted.last().getEndTime() == null)
            throw new ShiftAlreadyStartedException("Shift already ongoing!");
    }

    private void workTimeExceeded(Set<WorkHours> workHours) throws WorkTimeExceededException {
        LocalDateTime workTimeForDay = LocalDateTime.now().with(LocalTime.MIN);
        for (WorkHours w : workHours) {
            long numberOfSeconds = w.getStartTime().until(w.getEndTime(), ChronoUnit.SECONDS);
            workTimeForDay = workTimeForDay.plusSeconds(numberOfSeconds);
        }

        if (workTimeForDay.isAfter(LocalDateTime.now().with(LocalTime.MIN).plusHours(8)))
            throw new WorkTimeExceededException("Cannot start shift because you exceeded the 8 hours limit in last 24 hours!");
    }

    @Override
    public Page<WorkHours> findFilteredWorkHours(Integer driverId, Pageable pageable) {
        return workHoursRepository.findByDriverId(driverId, pageable);
    }

    @Override
    public Page<WorkHours> findFilteredWorkHours(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return workHoursRepository.findByDriverIdAndDateRange(driverId, from, to, pageable);
    }

    @Override
    public long getWorkingMinutesByDriverAtChosenDay(Integer driverId, LocalDate date)
    {
        List<WorkHours> workHoursList=workHoursRepository.findByDriverIdAndStartTimeBetween(driverId,date.atStartOfDay(),date.atTime(23,59,59));
        long sum=0;
        for(WorkHours workHours:workHoursList)
        {
            if(workHours.getEndTime()!=null)
                sum+=ChronoUnit.MINUTES.between(workHours.getStartTime(), workHours.getEndTime());
            else
                sum+=ChronoUnit.MINUTES.between(workHours.getStartTime(), LocalDateTime.now());
        }
        return sum;
    }
}
