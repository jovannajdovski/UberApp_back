package com.uberTim12.ihor.controller;

import com.uberTim12.ihor.dto.ResponseMessageDTO;
import com.uberTim12.ihor.dto.communication.ReasonDTO;
import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.*;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.seeders.SeedUtils;
import com.uberTim12.ihor.seeders.Seeder;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({ Seeder.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class RideControllerTest {
    @Value("${server.port}")
    private int serverPort;

    private final String serverPath = "http://localhost:8081/api";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SeedUtils seedUtils;

    private HttpHeaders headersPassenger;
    private HttpHeaders headersDriver;
    private HttpHeaders headersAdmin;
    private final JwtUtil jwtUtil = new JwtUtil();


    public void setUpPassenger(String email, String password) {
        restTemplate = new TestRestTemplate();
        headersPassenger = new HttpHeaders();

        UserCredentialsDTO dto = new UserCredentialsDTO(email, password);
        HttpEntity<UserCredentialsDTO> requestLogin = new HttpEntity<>(dto, headersPassenger);
        ResponseEntity<AuthTokenDTO> token = restTemplate.exchange(serverPath + "/user/login", HttpMethod.POST, requestLogin, AuthTokenDTO.class);
        headersPassenger.add("Authorization", "Bearer " + token.getBody().getAccessToken());
    }

    public void setUpDriver(String email, String password) {
        restTemplate = new TestRestTemplate();
        headersDriver = new HttpHeaders();

        UserCredentialsDTO dto = new UserCredentialsDTO(email, password);
        HttpEntity<UserCredentialsDTO> requestLogin = new HttpEntity<>(dto, headersDriver);
        ResponseEntity<AuthTokenDTO> token = restTemplate.exchange(serverPath + "/user/login", HttpMethod.POST, requestLogin, AuthTokenDTO.class);
        headersDriver.add("Authorization", "Bearer " + token.getBody().getAccessToken());
    }

    public void setUpAdmin(String email, String password) {
        restTemplate = new TestRestTemplate();
        headersAdmin = new HttpHeaders();

        UserCredentialsDTO dto = new UserCredentialsDTO(email, password);
        HttpEntity<UserCredentialsDTO> requestLogin = new HttpEntity<>(dto, headersAdmin);
        ResponseEntity<AuthTokenDTO> token = restTemplate.exchange(serverPath + "/user/login", HttpMethod.POST, requestLogin, AuthTokenDTO.class);
        headersAdmin.add("Authorization", "Bearer " + token.getBody().getAccessToken());
    }

    // Endpoint createRide
    @Test
    public void createRide_whenPassengerHasRole_returnsRide() {
        // Activate driver
        seedUtils.insertActiveDriver(Seeder.DRIVER_FIRST_ID, Seeder.LOCATION_FIRST_ID);

        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        LocationDTO startLocation = new LocationDTO("Bulevar Cara Lazara 90", 45.2405129, 19.8265563);
        LocationDTO endLocation = new LocationDTO("Bulevar Patrijaha Pavla 2", 45.23984, 19.82062);
        PathDTO path = new PathDTO(startLocation, endLocation);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(Seeder.PASSENGER_SECOND_ID);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateRideDTO rideDTO = new CreateRideDTO(
                paths,
                passengers,
                VehicleCategory.STANDARD,
                true,
                true,
                LocalDateTime.now()
        );

        HttpEntity<CreateRideDTO> createRideDTO = new HttpEntity<>(rideDTO, headersPassenger);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride", HttpMethod.POST, createRideDTO, RideFullDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(response.getBody().getPassengers().stream().iterator().next().getId(), passenger.getId());
        assertEquals(VehicleCategory.STANDARD, response.getBody().getVehicleType());
    }

    @Test
    public void createRide_whenInvalidCreateRideDTO_returnsForbidden() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        LocationDTO startLocation = new LocationDTO("Bulevar Cara Lazara 90", 45.2405129, 19.8265563);
        LocationDTO endLocation = new LocationDTO("Bulevar Patrijaha Pavla 2", 45.23984, 19.82062);
        PathDTO path = new PathDTO(startLocation, endLocation);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(1);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateRideDTO rideDTO = new CreateRideDTO();

        HttpEntity<CreateRideDTO> createRideDTO = new HttpEntity<>(rideDTO, headersPassenger);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride", HttpMethod.POST, createRideDTO, RideFullDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void createRide_whenNotLogged_returnsUnauthorized() {
        LocationDTO startLocation = new LocationDTO("Bulevar Cara Lazara 90", 45.2405129, 19.8265563);
        LocationDTO endLocation = new LocationDTO("Bulevar Patrijaha Pavla 2", 45.23984, 19.82062);
        PathDTO path = new PathDTO(startLocation, endLocation);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(1);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateRideDTO rideDTO = new CreateRideDTO(
                paths,
                passengers,
                VehicleCategory.STANDARD,
                true,
                true,
                LocalDateTime.now()
        );

        HttpEntity<CreateRideDTO> createRideDTO = new HttpEntity<>(rideDTO, headersPassenger);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride", HttpMethod.POST, createRideDTO, RideFullDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void createRide_whenDriverTries_returnsForbidden() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);

        LocationDTO startLocation = new LocationDTO("Bulevar Cara Lazara 90", 45.2405129, 19.8265563);
        LocationDTO endLocation = new LocationDTO("Bulevar Patrijaha Pavla 2", 45.23984, 19.82062);
        PathDTO path = new PathDTO(startLocation, endLocation);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(1);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateRideDTO rideDTO = new CreateRideDTO(
                paths,
                passengers,
                VehicleCategory.STANDARD,
                true,
                true,
                LocalDateTime.now()
        );

        HttpEntity<CreateRideDTO> createRideDTO = new HttpEntity<>(rideDTO, headersDriver);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride", HttpMethod.POST, createRideDTO, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void createRide_whenNoActiveDriver_returnsBadRequest() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        WorkHoursEndDTO workHoursEndDTO = new WorkHoursEndDTO(LocalDateTime.now());
        HttpEntity<WorkHoursEndDTO> changeWorkingHours = new HttpEntity<>(workHoursEndDTO, headersDriver);
        restTemplate.exchange(serverPath + "/driver/working-hour/" + Seeder.WORKHOURS_FIRST_ID, HttpMethod.PUT, changeWorkingHours, String.class);


        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        LocationDTO startLocation = new LocationDTO("Bulevar Cara Lazara 90", 45.2405129, 19.8265563);
        LocationDTO endLocation = new LocationDTO("Bulevar Patrijaha Pavla 2", 45.23984, 19.82062);
        PathDTO path = new PathDTO(startLocation, endLocation);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(1);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateRideDTO rideDTO = new CreateRideDTO(
                paths,
                passengers,
                VehicleCategory.STANDARD,
                true,
                true,
                LocalDateTime.now()
        );

        HttpEntity<CreateRideDTO> createRideDTO = new HttpEntity<>(rideDTO, headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride", HttpMethod.POST, createRideDTO, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Driving is not possible!", response.getBody());
    }

    @Test
    public void createRide_whenPassengerAlreadyHasRide_returnsBadRequest() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        restTemplate = new TestRestTemplate();
        headersDriver = new HttpHeaders();

        UserCredentialsDTO dto = new UserCredentialsDTO(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<UserCredentialsDTO> requestLogin = new HttpEntity<>(dto, headersDriver);
        ResponseEntity<AuthTokenDTO> token = restTemplate.exchange(serverPath + "/user/login", HttpMethod.POST, requestLogin, AuthTokenDTO.class);
        headersDriver.add("Authorization", "Bearer " + token.getBody().getAccessToken());

        WorkHoursStartDTO startDTO = new WorkHoursStartDTO(LocalDateTime.now());
        HttpEntity<WorkHoursStartDTO> requestWorkHours = new HttpEntity<>(startDTO, headersDriver);

        restTemplate.exchange(serverPath + "/driver/" + Seeder.DRIVER_FIRST_ID + "/working-hour", HttpMethod.POST, requestWorkHours, String.class);

        LocationDTO startLocation = new LocationDTO("Bulevar Cara Lazara 90", 45.2405129, 19.8265563);
        LocationDTO endLocation = new LocationDTO("Bulevar Patrijaha Pavla 2", 45.23984, 19.82062);
        PathDTO path = new PathDTO(startLocation, endLocation);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(Seeder.PASSENGER_FIRST_ID);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateRideDTO rideDTO = new CreateRideDTO(
                paths,
                passengers,
                VehicleCategory.STANDARD,
                true,
                true,
                LocalDateTime.now()
        );

        HttpEntity<CreateRideDTO> createRideDTO = new HttpEntity<>(rideDTO, headersPassenger);
        ResponseEntity<RideFullDTO> createdRide  = restTemplate.exchange(serverPath + "/ride", HttpMethod.POST, createRideDTO, RideFullDTO.class);
        HttpEntity<String> manipulateRide = new HttpEntity<>(null, headersDriver);
        restTemplate.exchange(serverPath + "/ride/" + createdRide.getBody().getId().toString() + "/accept", HttpMethod.PUT, manipulateRide, String.class);
        restTemplate.exchange(serverPath + "/ride/" + createdRide.getBody().getId().toString() + "/start", HttpMethod.PUT, manipulateRide, String.class);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride", HttpMethod.POST, createRideDTO, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Endpoint cancelRide
    @Test
    public void cancelRide_whenAppropriateDriverTries_returnsOK() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.PENDING,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID, rideID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> cancelRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/withdraw", HttpMethod.PUT, cancelRideRequest, RideFullDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideID, response.getBody().getId());
        assertEquals(RideStatus.CANCELED, response.getBody().getStatus());
    }

    @Test
    public void cancelRide_whenAppropriatePassengerTries_returnsOK() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.PENDING,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID, rideID);

        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> cancelRideRequest = new HttpEntity<>(null, headersPassenger);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/withdraw", HttpMethod.PUT, cancelRideRequest, RideFullDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideID, response.getBody().getId());
        assertEquals(RideStatus.CANCELED, response.getBody().getStatus());
    }

    @Test
    public void cancelRide_whenInvalidRoleTries_returnsForbidden() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.PENDING,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID, rideID);

        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> cancelRideRequest = new HttpEntity<>(null, headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/withdraw", HttpMethod.PUT, cancelRideRequest, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void cancelRide_whenRideDoesNotExist_returnsNotFound() {
        setUpAdmin(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> cancelRideRequest = new HttpEntity<>(null, headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + 1 + "/withdraw", HttpMethod.PUT, cancelRideRequest, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }

    @Test
    public void cancelRide_whenRideInWrongStatus_returnsBadRequest() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.REJECTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID, rideID);

        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> cancelRideRequest = new HttpEntity<>(null, headersPassenger);
        ResponseEntity<ResponseMessageDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/withdraw", HttpMethod.PUT, cancelRideRequest, ResponseMessageDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot cancel a ride that is not in status PENDING or STARTED!", response.getBody().getMessage());
    }

    @Test
    public void cancelRide_whenPassengerNotInRide_returnsNotFound() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.REJECTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> cancelRideRequest = new HttpEntity<>(null, headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/withdraw", HttpMethod.PUT, cancelRideRequest, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }

    // Endpoint startRide
    @Test
    public void startRide_whenAppropriateDriverTries_returnsOK() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.ACCEPTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_FIRST_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/start", HttpMethod.PUT, startRideRequest, RideFullDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideID, response.getBody().getId());
        assertEquals(RideStatus.STARTED, response.getBody().getStatus());
    }

    @Test
    public void startRide_whenRideDoesNotExist_returnsNotFound() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + 1 + "/start", HttpMethod.PUT, startRideRequest, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void startRide_whenAuthorizationNotProvided_returnsUnauthorized() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.ACCEPTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_FIRST_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, null);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/start", HttpMethod.PUT, startRideRequest, RideFullDTO.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void startRide_whenWrongRole_returnsForbidden() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.ACCEPTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_FIRST_ID);

        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/start", HttpMethod.PUT, startRideRequest, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void startRide_whenWrongRideStatus_returnsBadRequest() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.PENDING,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_FIRST_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<ResponseMessageDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/start", HttpMethod.PUT, startRideRequest, ResponseMessageDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot start a ride that is not in status ACCEPTED!", response.getBody().getMessage());
    }

    @Test
    public void startRide_whenRideInProgress_returnsBadRequest() {
        var firstRideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.STARTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_THIRD_ID, firstRideID);
        seedUtils.addPathToRide(firstRideID, Seeder.PATH_THIRD_ID);

        var secondRideId = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.ACCEPTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_FOURTH_ID, secondRideId);
        seedUtils.addPathToRide(secondRideId, Seeder.PATH_FIRST_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<ResponseMessageDTO> response = restTemplate.exchange(serverPath + "/ride/" + secondRideId + "/start", HttpMethod.PUT, startRideRequest, ResponseMessageDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot start a ride if you already have STARTED ride!", response.getBody().getMessage());
    }

    // Endpoint acceptRide
    @Test
    public void acceptRide_whenRideInProgress_returnsBadRequest() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.PENDING,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_THIRD_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_THIRD_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/accept", HttpMethod.PUT, startRideRequest, RideFullDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideID, response.getBody().getId());
        assertEquals(RideStatus.ACCEPTED, response.getBody().getStatus());
    }

    @Test
    public void acceptRide_whenRideDoesNotExist_returnsBadRequest() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + 1 + "/accept", HttpMethod.PUT, startRideRequest, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }

    @Test
    public void acceptRide_whenRoleInvalid_returnsForbidden() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + 1 + "/accept", HttpMethod.PUT, startRideRequest, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void acceptRide_whenRideNotInPending_returnsNotFound() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.ACCEPTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_THIRD_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_THIRD_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/accept", HttpMethod.PUT, startRideRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Endpoint endRide
    @Test
    public void endRide_whenAppropriateDriverEnds_returnsOK() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.STARTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_THIRD_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_THIRD_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/end", HttpMethod.PUT, startRideRequest, RideFullDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideID, response.getBody().getId());
        assertEquals(RideStatus.FINISHED, response.getBody().getStatus());
    }

    @Test
    public void endRide_whenRideDoesNotExist_returnsNotFound() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + 1 + "/end", HttpMethod.PUT, startRideRequest, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }

    @Test
    public void endRide_whenNotRideOfDriver_returnsNotFound() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_SECOND_ID,
                30.0,
                RideStatus.STARTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_THIRD_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_THIRD_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/end", HttpMethod.PUT, startRideRequest, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }

    @Test
    public void endRide_whenRideInWrongStatus_returnsBadRequest() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_SECOND_ID,
                30.0,
                RideStatus.PENDING,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_THIRD_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_THIRD_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        HttpEntity<CreateRideDTO> startRideRequest = new HttpEntity<>(null, headersDriver);
        ResponseEntity<ResponseMessageDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/end", HttpMethod.PUT, startRideRequest, ResponseMessageDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot end a ride that is not in status STARTED!", response.getBody().getMessage());
    }

    // Endpoint rejectRide
    @Test
    public void rejectRide_whenAppropriateDriver_returnsOK() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.ACCEPTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_THIRD_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_THIRD_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        ReasonDTO rejectReason = new ReasonDTO("I cant make it in time.");
        HttpEntity<ReasonDTO> startRideRequest = new HttpEntity<>(rejectReason, headersDriver);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/cancel", HttpMethod.PUT, startRideRequest, RideFullDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideID, response.getBody().getId());
        assertEquals(rejectReason.getReason(), response.getBody().getRejection().getReason());
    }

    @Test
    public void rejectRide_whenRideDoesNotExist_returnsNotFount() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        ReasonDTO rejectReason = new ReasonDTO("I cant make it in time.");
        HttpEntity<ReasonDTO> startRideRequest = new HttpEntity<>(rejectReason, headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + 1 + "/cancel", HttpMethod.PUT, startRideRequest, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }

    @Test
    public void rejectRide_whenRideInWrongStatus_returnsBadRequest() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_FIRST_ID,
                30.0,
                RideStatus.STARTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_THIRD_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_THIRD_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        ReasonDTO rejectReason = new ReasonDTO("I cant make it in time.");
        HttpEntity<ReasonDTO> startRideRequest = new HttpEntity<>(rejectReason, headersDriver);
        ResponseEntity<ResponseMessageDTO> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/cancel", HttpMethod.PUT, startRideRequest, ResponseMessageDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot cancel a ride that is not in status PENDING or ACCEPTED!", response.getBody().getMessage());
    }

    @Test
    public void rejectRide_authorizationHeaderDoesNotMatchDriver_returnsNotFound() {
        var rideID = seedUtils.insertRide(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                300.0,
                Seeder.DRIVER_THIRD_ID,
                30.0,
                RideStatus.ACCEPTED,
                true,
                true,
                Seeder.VEHICLETYPE_FIRST_ID,
                false,
                LocalDateTime.now());

        seedUtils.addPassengerToRide(Seeder.PASSENGER_THIRD_ID, rideID);
        seedUtils.addPathToRide(rideID, Seeder.PATH_THIRD_ID);

        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        ReasonDTO rejectReason = new ReasonDTO("I cant make it in time.");
        HttpEntity<ReasonDTO> startRideRequest = new HttpEntity<>(rejectReason, headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/" + rideID + "/cancel", HttpMethod.PUT, startRideRequest, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }
}
