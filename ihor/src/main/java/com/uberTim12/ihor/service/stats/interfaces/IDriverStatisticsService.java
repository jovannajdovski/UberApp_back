package com.uberTim12.ihor.service.stats.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeMap;

public interface IDriverStatisticsService{
    TreeMap<LocalDate, Integer> numberOfRidesPerDay(Integer id, LocalDateTime from, LocalDateTime to);

    TreeMap<LocalDate, Integer> distancePerDay(Integer id, LocalDateTime from, LocalDateTime to);

    Integer numberOfRejectedRides(Integer id, LocalDateTime from, LocalDateTime to);

    Integer numberOfAcceptedRides(Integer id, LocalDateTime from, LocalDateTime to);

    Integer totalWorkHours(Integer id, LocalDateTime from, LocalDateTime to);

    Integer totalIncome(Integer id, LocalDateTime from, LocalDateTime to);
}
