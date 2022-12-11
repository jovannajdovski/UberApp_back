package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.WorkHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface IWorkHoursRepository extends JpaRepository<WorkHours, Integer> {
    @Query("select W from WorkHours W where W.driver.id =?1")
    Page<WorkHours> findByDriverId(Integer driverId, Pageable pageable);
    @Query("select W from WorkHours W where W.driver.id =?1 and W.startTime between ?2 and ?3")
    Page<WorkHours> findByDriverIdAndDateRange(Integer driverId, LocalDate from, LocalDate to, Pageable pageable);
}