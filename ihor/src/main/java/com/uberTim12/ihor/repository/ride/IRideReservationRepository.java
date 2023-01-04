package com.uberTim12.ihor.repository.ride;

import com.uberTim12.ihor.model.ride.RideReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRideReservationRepository extends JpaRepository<RideReservation, Integer> {
}
