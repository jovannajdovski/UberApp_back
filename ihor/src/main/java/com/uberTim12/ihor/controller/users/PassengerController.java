package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.ride.RideDTO;
import com.uberTim12.ihor.dto.ride.RideNoStatusDTO;
import com.uberTim12.ihor.dto.users.PassengerDTO;
import com.uberTim12.ihor.dto.users.PassengerRegistrationDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.service.users.impl.PassengerService;
import com.uberTim12.ihor.service.users.impl.UserActivationService;
import com.uberTim12.ihor.util.ImageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;
    @Autowired
    private UserActivationService userActivationService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createPassenger(@RequestBody PassengerRegistrationDTO passengerDTO) {

        boolean exists = passengerService.exists(passengerDTO.getEmail());

        if(exists){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {

            Passenger passenger = passengerDTO.generatePassenger();
            passenger.setActive(false);

            passenger = passengerService.save(passenger);
            UserActivation ua = userActivationService.save(passenger);
            return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
        }
    }

    @GetMapping
    public ResponseEntity<?> getPassengersPage(Pageable page) {

        Page<Passenger> passengers = passengerService.findAll(page);

        List<PassengerDTO> passengersDTO = new ArrayList<>();
        for (Passenger p : passengers) {
            passengersDTO.add(new PassengerDTO(p));
        }

        ObjectListResponseDTO<PassengerDTO> res = new ObjectListResponseDTO<>(passengersDTO.size(),passengersDTO);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/activate/{activationId}")
    public ResponseEntity<?> activatePassenger(@PathVariable Integer activationId) {

        UserActivation ua = userActivationService.findById(activationId);

        if (ua == null || ua.getExpiryDate().isBefore(LocalDateTime.now())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {
            Passenger passenger = (Passenger) ua.getUser();

            passenger.setActive(true);
            passengerService.save(passenger);

            userActivationService.remove(activationId);

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getPassenger(@PathVariable Integer id) {

        Passenger passenger = passengerService.findById(id);

        if (passenger == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {
            return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updatePassenger(@PathVariable Integer id, @RequestBody PassengerRegistrationDTO passengerDTO) {

        Passenger passenger = passengerService.findById(id);

        if (passenger == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        passenger.setName(passengerDTO.getName());
        passenger.setSurname(passengerDTO.getSurname());
        passenger.setProfilePicture(ImageConverter.decodeToImage(passengerDTO.getProfilePicture()));
        passenger.setTelephoneNumber(passengerDTO.getTelephoneNumber());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setAddress(passengerDTO.getAddress());

        if (!passengerDTO.getPassword().equals("")){
            passenger.setPassword(passengerDTO.getPassword());
        }

        passenger = passengerService.save(passenger);

        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/ride")
    public ResponseEntity<?> getPassengerRidesPage(@PathVariable Integer id, Pageable page,
                                                   @RequestParam(required = false) String from,
                                                   @RequestParam(required = false) String to) {

        Passenger passenger = passengerService.findByIdWithRides(id);

        if (passenger == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
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
