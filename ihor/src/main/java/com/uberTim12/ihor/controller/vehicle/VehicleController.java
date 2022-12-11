package com.uberTim12.ihor.controller.vehicle;

import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.service.route.impl.LocationService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import com.uberTim12.ihor.service.vehicle.impl.VehicleService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/vehicle")
public class VehicleController {

    private final IVehicleService vehicleService;
    private final ILocationService locationService;

    @Autowired
    VehicleController(VehicleService vehicleService, LocationService locationService) {
        this.vehicleService = vehicleService;
        this.locationService = locationService;
    }

    @PutMapping(value = "/{vehicleId}/location", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeVehicleLocation(@PathVariable Integer vehicleId,
                                                   @RequestBody LocationDTO locationDTO) {

        Vehicle vehicle = vehicleService.findOne(vehicleId);

        if (vehicle == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Location location = new Location(locationDTO.getAddress(), locationDTO.getLatitude(), locationDTO.getLongitude());

        location = locationService.save(location);

        vehicle.setCurrentLocation(location);
        vehicleService.save(vehicle);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Coordinates successfully updated");
    }
}
