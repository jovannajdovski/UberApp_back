package com.uberTim12.ihor.dto.users;

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

    private LocalDateTime start;

    private LocalDateTime end;

    public WorkHoursDTO(WorkHours workHours)
    {
        this(workHours.getId(),
                workHours.getStartTime(),
                workHours.getEndTime()
        );
    }
}
