package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.ResponseMessageDTO;
import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.ride.RideNoStatusDTO;
import com.uberTim12.ihor.dto.users.PassengerDTO;
import com.uberTim12.ihor.dto.users.PassengerRegistrationDTO;
import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.exception.UserActivationExpiredException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.security.AuthUtil;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.service.users.impl.PassengerService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.service.users.interfaces.IUserActivationService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/passenger")
public class PassengerController {
    private final IPassengerService passengerService;
    private final IUserActivationService userActivationService;
    private final JwtUtil jwtUtil;

    @Autowired
    public PassengerController(PassengerService passengerService, IUserActivationService userActivationService, JwtUtil jwtUtil) {
        this.passengerService = passengerService;
        this.userActivationService = userActivationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createPassenger(@Valid @RequestBody PassengerRegistrationDTO passengerDTO) {
        Passenger passenger = passengerDTO.generatePassenger();
        try {
            passenger = passengerService.register(passenger);
            return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessageDTO("User with that email already exist!"));
        } catch (MessagingException | UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Error while sending mail!");
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObjectListResponseDTO<PassengerDTO>> getPassengersPage(Pageable page) {
        Page<Passenger> passengers = passengerService.getAll(page);

        List<PassengerDTO> passengersDTO = new ArrayList<>();
        for (Passenger p : passengers) {
            passengersDTO.add(new PassengerDTO(p));
        }

        ObjectListResponseDTO<PassengerDTO> res = new ObjectListResponseDTO<>((int) passengers.getTotalElements(), passengersDTO);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/activate/{activationId}")
    public ResponseEntity<?> activatePassenger(@Min(value = 100000) @Max(value = 999999) @PathVariable Integer activationId) {
        try {
            userActivationService.activate(activationId);
            return ResponseEntity.status(HttpStatus.OK).body("Successful account activation!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation with entered id does not exist!");
        } catch (UserActivationExpiredException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessageDTO("Activation expired. Register again!"));
        }
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public ResponseEntity<?> getPassenger(@Min(value = 1) @PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {

        if (Integer.parseInt(jwtUtil.extractId(authHeader.substring(7))) != id && (jwtUtil.extractRole(authHeader.substring(7)).equals("ROLE_PASSENGER")))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");

        try {
            Passenger passenger = passengerService.get(id);
            return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");
        }
    }

    @GetMapping(value = "/email/{email}")
    public ResponseEntity<?> getPassengerByEmail(@Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$") @PathVariable String email) {
        try {
            Passenger passenger = passengerService.findByEmail(email);
            if (passenger == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");
            return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> updatePassenger(@Min(value = 1) @PathVariable Integer id, @Valid @RequestBody PassengerRegistrationDTO passengerDTO, @RequestHeader("Authorization") String authHeader) {
        if (Integer.parseInt(jwtUtil.extractId(authHeader.substring(7))) != id)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");

        try {
            Passenger passenger = passengerService.update(id, passengerDTO.getName(), passengerDTO.getSurname(),
                    passengerDTO.getProfilePicture(), passengerDTO.getTelephoneNumber(), passengerDTO.getEmail(),
                    passengerDTO.getAddress(), passengerDTO.getPassword());
            return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");
        }
    }

    @GetMapping(value = "/{id}/ride")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public ResponseEntity<?> getPassengerRidesPage(@Min(value = 1) @PathVariable Integer id, Pageable page,
                                                   @RequestParam(required = false) String from,
                                                   @RequestParam(required = false) String to, @RequestHeader("Authorization") String authHeader) {

        if (Integer.parseInt(jwtUtil.extractId(authHeader.substring(7))) != id && (jwtUtil.extractRole(authHeader.substring(7)).equals("ROLE_PASSENGER")))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");

        Passenger passenger = passengerService.findByIdWithRides(id);

        if (passenger == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger does not exist!");
        }

        Page<Ride> rides;

        if (from == null || to == null)
            rides = passengerService.findAllById(passenger, page);
        else {
            LocalDateTime start = LocalDateTime.parse(from);
            LocalDateTime end = LocalDateTime.parse(to);
            rides = passengerService.findAllById(id, start, end, page);
        }

        List<RideNoStatusDTO> rideDTOs = new ArrayList<>();
        for (Ride r : rides)
            rideDTOs.add(new RideNoStatusDTO(r));

        ObjectListResponseDTO<RideNoStatusDTO> res = new ObjectListResponseDTO<>((int) rides.getTotalElements(), rideDTOs);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
