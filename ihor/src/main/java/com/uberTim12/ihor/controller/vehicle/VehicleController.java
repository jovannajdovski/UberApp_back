package com.uberTim12.ihor.controller.vehicle;

import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.service.route.impl.LocationService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import com.uberTim12.ihor.service.vehicle.impl.VehicleService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "api/vehicle")
public class VehicleController {

    private final IVehicleService vehicleService;

    @Autowired
    VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PutMapping(value = "/{vehicleId}/location", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeVehicleLocation(@PathVariable Integer vehicleId,
                                                   @RequestBody LocationDTO locationDTO) {
        Location location = new Location(locationDTO.getAddress(), locationDTO.getLatitude(), locationDTO.getLongitude());
        try {
            vehicleService.changeVehicleLocation(vehicleId, location);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Coordinates successfully updated");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle does not exist!");
        } catch (EntityPropertyIsNullException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }
}
