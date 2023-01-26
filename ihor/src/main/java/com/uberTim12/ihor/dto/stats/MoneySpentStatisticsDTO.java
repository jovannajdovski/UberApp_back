package com.uberTim12.ihor.dto.stats;

import com.uberTim12.ihor.model.stats.MoneySpentStatistics;
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
public class MoneySpentStatisticsDTO {
    private List<DailyMoneySpentDTO> amountPerDay;
    private Integer totalAmount;
    private Integer averageAmount;

    public MoneySpentStatisticsDTO(MoneySpentStatistics statistics) {
        this(new ArrayList<>(), statistics.getTotalAmount(), statistics.getAverageAmount());

        for (Map.Entry<LocalDate, Integer> entry : statistics.getAmountPerDay().entrySet()) {
            amountPerDay.add(new DailyMoneySpentDTO(entry.getKey().toString(), entry.getValue()));
        }
    }
}
