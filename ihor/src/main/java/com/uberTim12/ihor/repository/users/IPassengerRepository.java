package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPassengerRepository extends JpaRepository<Passenger, Integer> {

}
