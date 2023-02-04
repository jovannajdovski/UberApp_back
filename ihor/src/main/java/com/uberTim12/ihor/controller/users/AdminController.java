package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.stats.RideCountStatisticsDTO;
import com.uberTim12.ihor.dto.stats.RideDistanceStatisticsDTO;
import com.uberTim12.ihor.dto.users.AdminRegistrationDTO;
import com.uberTim12.ihor.dto.users.UserDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.stats.RideCountStatistics;
import com.uberTim12.ihor.model.stats.RideDistanceStatistics;
import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.stats.interfaces.IGlobalStatisticsService;
import com.uberTim12.ihor.service.users.impl.AdministratorService;
import com.uberTim12.ihor.service.users.interfaces.IAdministratorService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "api/admin")
public class AdminController {

    private final IAdministratorService adminService;
    private final IUserService userService;
    private final IGlobalStatisticsService globalStatisticsService;
    private final IRideService rideService;


    @Autowired
    public AdminController(AdministratorService adminService, IUserService userService, IGlobalStatisticsService globalStatisticsService, IRideService rideService) {
        this.adminService = adminService;
        this.userService = userService;
        this.globalStatisticsService = globalStatisticsService;
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
                                                    @RequestParam LocalDateTime to) {
        RideCountStatistics statistics = globalStatisticsService.numberOfRidesStatistics(from, to);
        return new ResponseEntity<>(new RideCountStatisticsDTO(statistics), HttpStatus.OK);
    }

    @GetMapping(value = "/distance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDistanceStatistics(@RequestParam LocalDateTime from,
                                                   @RequestParam LocalDateTime to) {
        RideDistanceStatistics statistics = globalStatisticsService.distancePerDayStatistics(from, to);
        return new ResponseEntity<>(new RideDistanceStatisticsDTO(statistics), HttpStatus.OK);
    }

    @GetMapping(value = "/ride/finished")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFinishedRides(Pageable paging,
                                              @RequestHeader("Authorization") String authHeader) {

        Page<Ride> rides;
        rides = this.rideService.findFilteredFinishedRidesAdmin(paging);

        List<RideFullDTO> rideDTOs = new ArrayList<>();
        for (Ride r : rides)
            rideDTOs.add(new RideFullDTO(r));

        ObjectListResponseDTO<RideFullDTO> res = new ObjectListResponseDTO<>((int) rides.getTotalElements(), rideDTOs);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
