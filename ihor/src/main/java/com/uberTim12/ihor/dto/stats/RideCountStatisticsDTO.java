package com.uberTim12.ihor.dto.stats;

import com.uberTim12.ihor.model.stats.RideCountStatistics;
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
public class RideCountStatisticsDTO {
    private List<DailyRideCountDTO> countPerDay;
    private Integer totalCount;
    private Double averageCount;

    public RideCountStatisticsDTO(RideCountStatistics statistics) {
        this(new ArrayList<>(), statistics.getTotalCount(), statistics.getAverageCount());

        for (Map.Entry<LocalDate, Integer> entry : statistics.getCountPerDay().entrySet()) {
            countPerDay.add(new DailyRideCountDTO(entry.getKey().toString(), entry.getValue()));
        }
    }
}
