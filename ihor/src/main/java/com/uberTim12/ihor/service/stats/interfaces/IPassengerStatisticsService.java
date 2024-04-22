package com.uberTim12.ihor.service.stats.interfaces;

import com.uberTim12.ihor.model.stats.MoneySpentStatistics;
import com.uberTim12.ihor.model.stats.RideCountStatistics;
import com.uberTim12.ihor.model.stats.RideDistanceStatistics;

import java.time.LocalDateTime;

public interface IPassengerStatisticsService {
    RideCountStatistics numberOfRidesStatistics(Integer id, LocalDateTime from, LocalDateTime to);

    MoneySpentStatistics moneySpentStatistics(Integer id, LocalDateTime from, LocalDateTime to);

    RideDistanceStatistics distancePerDayStatistics(Integer id, LocalDateTime from, LocalDateTime to);
}
