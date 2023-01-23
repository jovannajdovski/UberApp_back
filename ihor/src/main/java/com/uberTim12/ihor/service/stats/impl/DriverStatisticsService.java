package com.uberTim12.ihor.service.stats.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.stats.interfaces.IDriverStatisticsService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TreeMap;

@Service
public class DriverStatisticsService implements IDriverStatisticsService {

    private final IRideService rideService;
    private final IWorkHoursService workHoursService;

    @Autowired
    public DriverStatisticsService(IRideService rideService, IWorkHoursService workHoursService) {
        this.rideService = rideService;
        this.workHoursService = workHoursService;
    }

    @Override
    public TreeMap<LocalDate, Integer> numberOfRidesPerDay(Integer id, LocalDateTime from, LocalDateTime to) {
        TreeMap<LocalDate, Integer> ridesPerDay = initializeDayMap(from, to);

        List<Ride> rides = rideService.findRidesWithStatusForDriver(id, RideStatus.FINISHED, from, to);
        for (Ride r : rides) {
            LocalDate startDate = r.getStartTime().toLocalDate();
            ridesPerDay.put(startDate, ridesPerDay.get(startDate) + 1);
        }
        return ridesPerDay;
    }

    @Override
    public TreeMap<LocalDate, Integer> distancePerDay(Integer id, LocalDateTime from, LocalDateTime to) {
        TreeMap<LocalDate, Integer> distancePerDay = initializeDayMap(from, to);

        List<Ride> rides = rideService.findRidesWithStatusForDriver(id, RideStatus.FINISHED, from, to);
        for (Ride r : rides) {
            LocalDate startDate = r.getStartTime().toLocalDate();
            distancePerDay.put(startDate, distancePerDay.get(startDate) + rides.size());
        }
        return distancePerDay;
    }

    private TreeMap<LocalDate, Integer> initializeDayMap(LocalDateTime from, LocalDateTime to) {
        TreeMap<LocalDate, Integer> dayMap = new TreeMap<>();
        for (LocalDate date = from.toLocalDate(); date.isBefore(to.toLocalDate()); date = date.plusDays(1))
            dayMap.put(date, 0);

        return dayMap;
    }

    @Override
    public Integer numberOfRejectedRides(Integer id, LocalDateTime from, LocalDateTime to) {
       return rideService.findRidesWithStatusForDriver(id, RideStatus.REJECTED, from, to).size();
    }

    @Override
    public Integer numberOfAcceptedRides(Integer id, LocalDateTime from, LocalDateTime to) {
        return rideService.findRidesWithStatusForDriver(id, RideStatus.REJECTED, from, to).size();
    }

    @Override
    public Integer totalWorkHours(Integer id, LocalDateTime from, LocalDateTime to) {
        long numberOfHours = 0L;
        List<WorkHours> workHours = workHoursService.findAll(id, from, to);
        for (WorkHours w : workHours) {
            numberOfHours += w.getStartTime().until(w.getEndTime(), ChronoUnit.HOURS);
        }

        return (int) numberOfHours;
    }

    @Override
    public Integer totalIncome(Integer id, LocalDateTime from, LocalDateTime to) {
        int income = 0;
        List<Ride> finishedRides = rideService.findRidesWithStatusForDriver(id, RideStatus.FINISHED, from, to);
        for (Ride r : finishedRides)
            income += r.getTotalPrice().intValue();

        return income;
    }
}
