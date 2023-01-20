package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.dto.vehicle.VehicleDTO;
import com.uberTim12.ihor.model.ride.ActiveDriver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActiveDriverDTO {
    private VehicleDTO vehicle;
    private LocationDTO location;
    private boolean free;

}
