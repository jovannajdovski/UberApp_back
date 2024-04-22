package com.uberTim12.ihor.dto.stats;

import com.uberTim12.ihor.model.stats.RideDistanceStatistics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideDistanceStatisticsDTO {
    private List<DailyRideDistanceDTO> distancePerDay;
    private Integer totalDistance;
    private Integer AverageDistance;

    public RideDistanceStatisticsDTO(RideDistanceStatistics statistics) {
        this(new ArrayList<>(), statistics.getTotalDistance(), statistics.getAverageDistance());

        for (Map.Entry<LocalDate, Integer> entry : statistics.getDistancePerDay().entrySet()) {
            distancePerDay.add(new DailyRideDistanceDTO(entry.getKey().toString(), entry.getValue()));
        }
    }
}
