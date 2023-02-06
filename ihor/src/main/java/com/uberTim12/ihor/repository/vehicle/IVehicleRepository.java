package com.uberTim12.ihor.repository.vehicle;

import com.uberTim12.ihor.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVehicleRepository extends JpaRepository<Vehicle, Integer> {

}
