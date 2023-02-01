package com.uberTim12.ihor.service.stats.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.stats.DriverStatistics;
import com.uberTim12.ihor.model.stats.RideDistanceStatistics;
import com.uberTim12.ihor.model.stats.RideCountStatistics;
import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.impl.LocationService;
import com.uberTim12.ihor.service.stats.interfaces.IDriverStatisticsService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TreeMap;

@Service
public class DriverStatisticsService implements IDriverStatisticsService {

    private final IRideService rideService;
    private final IWorkHoursService workHoursService;
    private final LocationService locationService;

    @Autowired
    public DriverStatisticsService(IRideService rideService, IWorkHoursService workHoursService,
                                   LocationService locationService) {
        this.rideService = rideService;
        this.workHoursService = workHoursService;
        this.locationService = locationService;
    }

    @Override
    public RideCountStatistics numberOfRidesStatistics(Integer id, LocalDateTime from, LocalDateTime to) {
        TreeMap<LocalDate, Integer> ridesPerDay = initializeDayMap(from, to);
        int totalCount = 0;
        double avgCount = 0d;

        List<Ride> rides = rideService.findRidesWithStatusForDriver(id, RideStatus.FINISHED, from, to);
        for (Ride r : rides) {
            LocalDate startDate = r.getStartTime().toLocalDate();
            ridesPerDay.put(startDate, ridesPerDay.get(startDate) + 1);
            totalCount += 1;
            avgCount += 1;
        }
        avgCount = avgCount / ridesPerDay.size();
        DecimalFormat df = new DecimalFormat("#.##");

        return new RideCountStatistics(ridesPerDay, totalCount, Double.parseDouble(df.format(avgCount)));
    }

    @Override
    public RideDistanceStatistics distancePerDayStatistics(Integer id, LocalDateTime from, LocalDateTime to) {
        TreeMap<LocalDate, Double> distancePerDay = initializeDayMapDouble(from, to);
        double totalDistance = 0d;
        double avgDistance = 0d;

        List<Ride> rides = rideService.findRidesWithStatusForDriver(id, RideStatus.FINISHED, from, to);
        for (Ride r : rides) {
            LocalDate startDate = r.getStartTime().toLocalDate();
            Double distance = calculateDistance(r);
            distancePerDay.put(startDate, distancePerDay.get(startDate) + distance);
            totalDistance += distance;
            avgDistance += distance;
        }
        avgDistance = avgDistance / distancePerDay.size();

        return new RideDistanceStatistics(doubleToIntMap(distancePerDay), (int) totalDistance, (int) avgDistance);
    }

    private Double calculateDistance(Ride r) {
        Double distance = 0d;
        for (Path p : r.getPaths()) {
            try {
                distance += locationService.calculateDistance(p.getStartPoint(), p.getEndPoint());
            } catch (IOException | ParseException e) {
                return 0d;
            }
        }

        return distance;
    }

    private TreeMap<LocalDate, Integer> initializeDayMap(LocalDateTime from, LocalDateTime to) {
        TreeMap<LocalDate, Integer> dayMap = new TreeMap<>();
        for (LocalDate date = from.toLocalDate(); date.isBefore(to.toLocalDate().plusDays(1)); date = date.plusDays(1))
            dayMap.put(date, 0);

        return dayMap;
    }

    private TreeMap<LocalDate, Double> initializeDayMapDouble(LocalDateTime from, LocalDateTime to) {
        TreeMap<LocalDate, Double> dayMap = new TreeMap<>();
        for (LocalDate date = from.toLocalDate(); date.isBefore(to.toLocalDate().plusDays(1)); date = date.plusDays(1))
            dayMap.put(date, 0D);

        return dayMap;
    }

    private TreeMap<LocalDate, Integer> doubleToIntMap(TreeMap<LocalDate, Double> doubleMap) {
        TreeMap<LocalDate, Integer> dayMap = new TreeMap<>();
        for (LocalDate date : doubleMap.keySet())
            dayMap.put(date, doubleMap.get(date).intValue());
        return dayMap;
    }

    @Override
    public DriverStatistics getDriverStatistics(Integer id, LocalDateTime from, LocalDateTime to) {
        Integer rejectedCount = numberOfRejectedRides(id, from ,to);
        Integer acceptedCount = numberOfAcceptedRides(id, from ,to);
        Integer totalWorkHours = totalWorkHours(id, from ,to);
        Integer totalIncome = totalIncome(id, from ,to);

        return new DriverStatistics(rejectedCount, acceptedCount, totalWorkHours, totalIncome);
    }

    private Integer numberOfRejectedRides(Integer id, LocalDateTime from, LocalDateTime to) {
       return rideService.findRidesWithStatusForDriver(id, RideStatus.REJECTED, from, to).size();
    }

    private Integer numberOfAcceptedRides(Integer id, LocalDateTime from, LocalDateTime to) {
        return rideService.findAcceptedRides(id, from, to).size();
    }

    private Integer totalWorkHours(Integer id, LocalDateTime from, LocalDateTime to) {
        long numberOfHours = 0L;
        List<WorkHours> workHours = workHoursService.findAll(id, from, to);
        for (WorkHours w : workHours) {
            if(w.getEndTime() != null)
                numberOfHours += w.getStartTime().until(w.getEndTime(), ChronoUnit.HOURS);
        }

        return (int) numberOfHours;
    }

    private Integer totalIncome(Integer id, LocalDateTime from, LocalDateTime to) {
        Double income = 0d;
        List<Ride> finishedRides = rideService.findRidesWithStatusForDriver(id, RideStatus.FINISHED, from, to);
        for (Ride r : finishedRides)
            income += r.getTotalPrice();

        return income.intValue();
    }
}
