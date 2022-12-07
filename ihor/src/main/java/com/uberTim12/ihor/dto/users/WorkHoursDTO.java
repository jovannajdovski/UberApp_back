package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.WorkHours;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkHoursDTO {

    private Integer id;
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Driver driver;

    public WorkHoursDTO(WorkHours workHours)
    {
        this(workHours.getId(),
                workHours.getStartTime(),
                workHours.getEndTime(),
                workHours.getDriver()
        );
    }
}
