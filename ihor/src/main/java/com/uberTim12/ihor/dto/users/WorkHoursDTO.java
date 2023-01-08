package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.WorkHours;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
