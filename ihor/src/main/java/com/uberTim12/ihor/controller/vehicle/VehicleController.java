package com.uberTim12.ihor.controller.vehicle;

import com.uberTim12.ihor.dto.ResponseMessageDTO;
import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.service.vehicle.impl.VehicleService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@RequestMapping(value = "api/vehicle")
public class VehicleController {

    private final IVehicleService vehicleService;
    private final JwtUtil jwtUtil;

    @Autowired
    VehicleController(VehicleService vehicleService, JwtUtil jwtUtil) {
        this.vehicleService = vehicleService;
        this.jwtUtil = jwtUtil;
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping(value = "/{vehicleId}/location", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeVehicleLocation(@Min(value = 1) @PathVariable Integer vehicleId,
                                                        @Valid @RequestBody LocationDTO locationDTO,
                                                        @RequestHeader("Authorization") String authHeader) {

        String jwtToken = authHeader.substring(7);
        Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));

        try {
            Vehicle vehicle = vehicleService.get(vehicleId);
            if (!Objects.equals(vehicle.getDriver().getId(), loggedId)){
                return new ResponseEntity<>("Vehicle does not exist!", HttpStatus.NOT_FOUND);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Vehicle does not exist!", HttpStatus.NOT_FOUND);
        }

        Location location = new Location(locationDTO.getAddress(), locationDTO.getLatitude(), locationDTO.getLongitude());
        try {
            vehicleService.changeVehicleLocation(vehicleId, location);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Coordinates successfully updated");
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Vehicle does not exist!", HttpStatus.NOT_FOUND);
        } catch (EntityPropertyIsNullException e) {
            return new ResponseEntity<>(new ResponseMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }
}
