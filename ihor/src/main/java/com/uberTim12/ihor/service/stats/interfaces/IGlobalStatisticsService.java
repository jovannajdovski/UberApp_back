package com.uberTim12.ihor.service.stats.interfaces;

import com.uberTim12.ihor.model.stats.RideCountStatistics;
import com.uberTim12.ihor.model.stats.RideDistanceStatistics;

import java.time.LocalDateTime;

public interface IGlobalStatisticsService {
    RideCountStatistics numberOfRidesStatistics(LocalDateTime from, LocalDateTime to);

    RideDistanceStatistics distancePerDayStatistics(LocalDateTime from, LocalDateTime to);
}
