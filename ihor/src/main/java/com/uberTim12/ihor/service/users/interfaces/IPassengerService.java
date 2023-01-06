package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPassengerService extends IJPAService<Passenger> {

    boolean exists(String email);

    Passenger findByIdWithRides(Integer id);
}
