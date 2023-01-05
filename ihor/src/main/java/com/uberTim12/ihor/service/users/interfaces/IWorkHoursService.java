package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IWorkHoursService extends IJPAService<WorkHours> {

    Page<WorkHours> findFilteredWorkHours(Integer driverId, Pageable pageable);
    Page<WorkHours> findFilteredWorkHours(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
