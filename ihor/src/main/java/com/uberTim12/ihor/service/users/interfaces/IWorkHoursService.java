package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.exception.ShiftAlreadyStartedException;
import com.uberTim12.ihor.exception.WorkTimeExceededException;
import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IWorkHoursService extends IJPAService<WorkHours> {
    WorkHours endShift(Integer workHoursId, LocalDateTime endTime) throws EntityNotFoundException;
    WorkHours startShift(Integer driverId, WorkHours workHours) throws EntityNotFoundException, EntityPropertyIsNullException, ShiftAlreadyStartedException, WorkTimeExceededException;
    Page<WorkHours> findFilteredWorkHours(Integer driverId, Pageable pageable);
    Page<WorkHours> findFilteredWorkHours(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable);
    long getWorkingMinutesByDriverAtChoosedDay(Integer driverId, LocalDate date);
}
