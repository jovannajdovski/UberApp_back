package com.uberTim12.ihor.repository.vehicle;

import com.uberTim12.ihor.model.vehicle.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVehicleTypeRepository extends JpaRepository<VehicleType, Integer> {

}
