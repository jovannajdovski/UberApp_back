package com.uberTim12.ihor.repository.ride;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRideRepository extends JpaRepository<Ride, Integer> {


}
