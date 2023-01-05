package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.repository.users.IWorkHoursRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WorkHoursService extends JPAService<WorkHours> implements IWorkHoursService {

    private final IWorkHoursRepository workHoursRepository;

    @Autowired
    WorkHoursService(IWorkHoursRepository workHoursRepository) {
        this.workHoursRepository = workHoursRepository;
    }

    @Override
    protected JpaRepository<WorkHours, Integer> getEntityRepository() {
        return workHoursRepository;
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
