package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.users.AdminRegistrationDTO;
import com.uberTim12.ihor.dto.users.UserDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.impl.AdministratorService;
import com.uberTim12.ihor.service.users.interfaces.IAdministratorService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/admin")
public class AdminController {

    private final IAdministratorService adminService;
    private final IRideService rideService;

    @Autowired
    public AdminController(AdministratorService adminService, IRideService rideService) {
        this.adminService = adminService;
        this.rideService = rideService;
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getAdmin(@PathVariable Integer id) {
        try {
            Administrator admin = adminService.get(id);
            return new ResponseEntity<>(new UserDTO(admin), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrator does not exist!");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateAdmin(@PathVariable Integer id, @Valid @RequestBody AdminRegistrationDTO adminDTO) {
        try {
            Administrator admin = adminService.update(id, adminDTO.getName(), adminDTO.getSurname(), adminDTO.getProfilePicture(),
                    adminDTO.getTelephoneNumber(), adminDTO.getEmail(), adminDTO.getAddress(), adminDTO.getPassword());
            return new ResponseEntity<>(new UserDTO(admin), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrator does not exist!");
        }

    }

    @GetMapping(value = "/ride/finished")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFinishedRides(@Min(value = 0) @RequestParam int page,
                                               @Min(value = 1) @RequestParam int size,
                                               @RequestHeader("Authorization") String authHeader) {

        Pageable paging = PageRequest.of(page, size);

        Page<Ride> rides;
        rides = rideService.findFilteredFinishedRidesAdmin(paging);

        List<RideFullDTO> rideDTOs = new ArrayList<>();
        for (Ride r : rides)
            rideDTOs.add(new RideFullDTO(r));

        ObjectListResponseDTO<RideFullDTO> res = new ObjectListResponseDTO<>((int) rides.getTotalElements(), rideDTOs);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
