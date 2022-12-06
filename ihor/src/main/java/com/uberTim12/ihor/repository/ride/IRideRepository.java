package com.uberTim12.ihor.repository.ride;

import com.uberTim12.ihor.model.ride.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface IRideRepository extends JpaRepository<Ride, Integer> {
    @Query("select R from Ride R where R.driver.id =?1")
    Page<Ride> findByDriverId(Integer driverId, Pageable pageable);
    @Query("select R from Ride R where R.driver.id =?1 and R.startTime between ?2 and ?3")
    Page<Ride> findByDriverIdAndDateRange(Integer driverId, LocalDate from, LocalDate to, Pageable pageable);
}
