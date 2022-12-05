package com.uberTim12.ihor.controller;

import com.uberTim12.ihor.dto.users.PassengerDTO;
import com.uberTim12.ihor.dto.users.PassengerRegistrationDTO;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.service.users.impl.PassengerService;
import com.uberTim12.ihor.service.users.impl.UserActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private UserActivationService userActivationService;

    @GetMapping
    public ResponseEntity<List<PassengerDTO>> getPassengersPage(Pageable page) {

        Page<Passenger> passengers = passengerService.findAll(page);

        List<PassengerDTO> passengersDTO = new ArrayList<>();
        for (Passenger p : passengers) {
            passengersDTO.add(new PassengerDTO(p));
        }

        return new ResponseEntity<>(passengersDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PassengerDTO> getStudent(@PathVariable Integer id) {

        Passenger passenger = passengerService.findById(id);

        if (passenger == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PassengerDTO> createPassenger(@RequestBody PassengerRegistrationDTO passengerDTO) {

        Passenger passenger = new Passenger();
        passenger.setName(passengerDTO.getName());
        passenger.setSurname(passengerDTO.getSurname());
        passenger.setProfilePicture(passengerDTO.getProfilePicture());
        passenger.setTelephoneNumber(passengerDTO.getTelephoneNumber());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setAddress(passengerDTO.getAddress());
        passenger.setPassword(passengerDTO.getPassword());
        passenger.setActive(false);

        passenger = passengerService.save(passenger);
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.CREATED);
    }

    @PostMapping("/{activationId}")
    public ResponseEntity<Void> activatePassenger(@PathVariable Integer activationId) {

        Passenger passenger = passengerService.findById(activationId);

        if (passenger == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        passenger.setActive(true);

        UserActivation userActivation = new UserActivation();
        userActivation.setUser(passenger);
        userActivation.setCreationDate(LocalDateTime.now());
        userActivation.setExpiryDate(userActivation.getCreationDate().plusYears(1));

        userActivation = userActivationService.save(userActivation);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
