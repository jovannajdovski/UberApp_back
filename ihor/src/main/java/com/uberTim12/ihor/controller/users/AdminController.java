package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.stats.RideCountStatisticsDTO;
import com.uberTim12.ihor.dto.stats.RideDistanceStatisticsDTO;
import com.uberTim12.ihor.dto.users.AdminRegistrationDTO;
import com.uberTim12.ihor.dto.users.UserDTO;
import com.uberTim12.ihor.model.stats.RideCountStatistics;
import com.uberTim12.ihor.model.stats.RideDistanceStatistics;
import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.service.stats.interfaces.IGlobalStatisticsService;
import com.uberTim12.ihor.service.users.impl.AdministratorService;
import com.uberTim12.ihor.service.users.interfaces.IAdministratorService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "api/admin")
public class AdminController {

    private final IAdministratorService adminService;
    private final IUserService userService;
    private final IGlobalStatisticsService globalStatisticsService;


    @Autowired
    public AdminController(AdministratorService adminService, IUserService userService, IGlobalStatisticsService globalStatisticsService) {
        this.adminService = adminService;
        this.userService = userService;
        this.globalStatisticsService = globalStatisticsService;
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

    @PutMapping(value = "/{id}")
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

    @GetMapping(value = "/ride-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRideCountStatistics(@RequestParam LocalDateTime from,
                                                    @RequestParam LocalDateTime to)
    {
        RideCountStatistics statistics = globalStatisticsService.numberOfRidesStatistics(from, to);
        return new ResponseEntity<>(new RideCountStatisticsDTO(statistics), HttpStatus.OK);
    }

    @GetMapping(value = "/distance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDistanceStatistics(@RequestParam LocalDateTime from,
                                                   @RequestParam LocalDateTime to)
    {
        RideDistanceStatistics statistics = globalStatisticsService.distancePerDayStatistics(from, to);
        return new ResponseEntity<>(new RideDistanceStatisticsDTO(statistics), HttpStatus.OK);
    }
}
