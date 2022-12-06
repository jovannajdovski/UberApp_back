package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.WorkHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IWorkHoursService {

    WorkHours save(WorkHours workHours);
    public WorkHours findOne(Integer id);
    Page<WorkHours> findFilteredWorkHours(Integer driverId, Pageable pageable);
    Page<WorkHours> findFilteredWorkHours(Integer driverId, LocalDate from, LocalDate to, Pageable pageable);
}
