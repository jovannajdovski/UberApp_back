package com.uberTim12.ihor.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uberTim12.ihor.model.users.WorkHours;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkHoursDTO {
    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime end;

    public WorkHoursDTO(WorkHours workHours)
    {
        this(workHours.getId(),
                workHours.getStartTime(),
                workHours.getEndTime()
        );
    }
}
