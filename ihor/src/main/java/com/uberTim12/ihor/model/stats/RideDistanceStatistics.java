package com.uberTim12.ihor.model.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.TreeMap;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideDistanceStatistics {
    private TreeMap<LocalDate, Integer> distancePerDay;
    private Integer totalDistance;
    private Integer averageDistance;
}
