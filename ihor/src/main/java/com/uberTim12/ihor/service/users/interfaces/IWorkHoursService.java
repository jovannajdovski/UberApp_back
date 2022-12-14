package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.WorkHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IWorkHoursService {

    WorkHours save(WorkHours workHours);
    WorkHours findOne(Integer id);
    Page<WorkHours> findFilteredWorkHours(Integer driverId, Pageable pageable);
    Page<WorkHours> findFilteredWorkHours(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
