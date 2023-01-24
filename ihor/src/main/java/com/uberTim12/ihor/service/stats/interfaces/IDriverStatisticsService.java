package com.uberTim12.ihor.service.stats.interfaces;

import com.uberTim12.ihor.model.stats.DriverStatistics;
import com.uberTim12.ihor.model.stats.RideDistanceStatistics;
import com.uberTim12.ihor.model.stats.RideCountStatistics;

import java.time.LocalDateTime;

public interface IDriverStatisticsService{
    RideCountStatistics numberOfRidesStatistics(Integer id, LocalDateTime from, LocalDateTime to);

    RideDistanceStatistics distancePerDayStatistics(Integer id, LocalDateTime from, LocalDateTime to);

    DriverStatistics getDriverStatistics(Integer id, LocalDateTime from, LocalDateTime to);
}
