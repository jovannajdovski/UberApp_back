package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.ride.RideNoStatusDTO;
import com.uberTim12.ihor.dto.users.PassengerDTO;
import com.uberTim12.ihor.dto.users.PassengerRegistrationDTO;
import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.exception.UserActivationExpiredException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.service.users.impl.PassengerService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.service.users.interfaces.IUserActivationService;
import com.uberTim12.ihor.util.ImageConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/passenger")
public class PassengerController {
    private final IPassengerService passengerService;
    private final IUserActivationService userActivationService;

    @Autowired
    public PassengerController(PassengerService passengerService, IUserActivationService userActivationService) {
        this.passengerService = passengerService;
        this.userActivationService = userActivationService;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<PassengerDTO> createPassenger(@RequestBody PassengerRegistrationDTO passengerDTO) {
        Passenger passenger = passengerDTO.generatePassenger();
        try {
            passenger = passengerService.register(passenger);
            return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
        } catch (EmailAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with that email already exist!");
        }
    }

    @GetMapping
    public ResponseEntity<ObjectListResponseDTO<PassengerDTO>> getPassengersPage(Pageable page) {
        Page<Passenger> passengers = passengerService.getAll(page);

        List<PassengerDTO> passengersDTO = new ArrayList<>();
        for (Passenger p : passengers) {
            passengersDTO.add(new PassengerDTO(p));
        }

        ObjectListResponseDTO<PassengerDTO> res = new ObjectListResponseDTO<>(passengersDTO.size(),passengersDTO);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/activate/{activationId}")
    public ResponseEntity<String> activatePassenger(@PathVariable Integer activationId) {
        try {
            userActivationService.activate(activationId);
            return ResponseEntity.status(HttpStatus.OK).body("Successful account activation!");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activation with entered id does not exist!");
        } catch (UserActivationExpiredException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activation expired. Register again!");
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PassengerDTO> getPassenger(@PathVariable Integer id) {
        try {
            Passenger passenger = passengerService.get(id);
            return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger does not exist!");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<PassengerDTO> updatePassenger(@PathVariable Integer id, @RequestBody PassengerRegistrationDTO passengerDTO) {
        try {
            Passenger passenger = passengerService.update(id, passengerDTO.getName(), passengerDTO.getSurname(),
                    passengerDTO.getProfilePicture(), passengerDTO.getTelephoneNumber(), passengerDTO.getEmail(),
                    passengerDTO.getAddress(), passengerDTO.getPassword());
            return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger does not exist!");
        }
    }

    @GetMapping(value = "/{id}/ride")
    public ResponseEntity<ObjectListResponseDTO<RideNoStatusDTO>> getPassengerRidesPage(@PathVariable Integer id, Pageable page,
                                                   @RequestParam(required = false) String from,
                                                   @RequestParam(required = false) String to) {

        Passenger passenger = passengerService.findByIdWithRides(id);

        if (passenger == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger does not exist!");
        }

        Page<Ride> rides;

        if (from == null || to == null)
            rides = passengerService.findAllById(passenger, page);
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime start =  LocalDate.parse(from, formatter).atStartOfDay();
            LocalDateTime end =  LocalDate.parse(to, formatter).atStartOfDay();
            rides = passengerService.findAllById(id, start, end, page);
        }

        List<RideNoStatusDTO> rideDTOs = new ArrayList<>();
        for (Ride r : rides)
            rideDTOs.add(new RideNoStatusDTO(r));

        ObjectListResponseDTO<RideNoStatusDTO> res = new ObjectListResponseDTO<>(rideDTOs.size(),rideDTOs);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
