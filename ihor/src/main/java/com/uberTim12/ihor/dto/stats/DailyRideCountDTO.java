package com.uberTim12.ihor.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyRideCountDTO {
    private String day;
    private Integer count;
}
