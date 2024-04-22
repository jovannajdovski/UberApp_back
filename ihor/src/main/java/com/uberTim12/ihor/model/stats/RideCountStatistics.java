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
public class RideCountStatistics {
    private TreeMap<LocalDate, Integer> countPerDay;
    private Integer totalCount;
    private Double averageCount;
}
