package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.repository.users.IWorkHoursRepository;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WorkHoursService implements IWorkHoursService {

    private final IWorkHoursRepository workHoursRepository;

    @Autowired
    WorkHoursService(IWorkHoursRepository workHoursRepository) {
        this.workHoursRepository = workHoursRepository;
    }

    @Override
    public WorkHours save(WorkHours workHours) {
        return workHoursRepository.save(workHours);
    }

    public WorkHours findOne(Integer id) {
        return workHoursRepository.findById(id).orElse(null);
    }

    @Override
    public Page<WorkHours> findFilteredWorkHours(Integer driverId, Pageable pageable) {
        return workHoursRepository.findByDriverId(driverId, pageable);
    }

    @Override
    public Page<WorkHours> findFilteredWorkHours(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return workHoursRepository.findByDriverIdAndDateRange(driverId, from, to, pageable);
    }
}
