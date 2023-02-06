package com.uberTim12.ihor.controller;

import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.AuthTokenDTO;
import com.uberTim12.ihor.dto.users.UserCredentialsDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.dto.users.WorkHoursStartDTO;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.security.JwtUtil;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({ Seeder.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class RideControllerTest {
    @Value("${server.port}")
    private int serverPort;

    private final String serverPath = "http://localhost:8080/api";

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headersPassenger;
    private HttpHeaders headersDriver;
    private HttpHeaders headersAdmin;
    private final JwtUtil jwtUtil = new JwtUtil();


    public void setUpPassenger() {
        restTemplate = new TestRestTemplate();
        headersPassenger = new HttpHeaders();

        UserCredentialsDTO dto = new UserCredentialsDTO("peki@gmail.com", "NekaSifra123");
        HttpEntity<UserCredentialsDTO> requestLogin = new HttpEntity<>(dto, headersPassenger);
        ResponseEntity<AuthTokenDTO> token = restTemplate.exchange(serverPath + "/user/login", HttpMethod.POST, requestLogin, AuthTokenDTO.class);
        headersPassenger.add("Authorization", "Bearer " + token.getBody().getAccessToken());
    }

    public void setUpDriver() {
        restTemplate = new TestRestTemplate();
        headersDriver = new HttpHeaders();

        UserCredentialsDTO dto = new UserCredentialsDTO("marinko@gmail.com", "NekaSifra123");
        HttpEntity<UserCredentialsDTO> requestLogin = new HttpEntity<>(dto, headersDriver);
        ResponseEntity<AuthTokenDTO> token = restTemplate.exchange(serverPath + "/user/login", HttpMethod.POST, requestLogin, AuthTokenDTO.class);
        headersDriver.add("Authorization", "Bearer " + token.getBody().getAccessToken());
    }

    public void setUpAdmin() {
        restTemplate = new TestRestTemplate();
        headersAdmin = new HttpHeaders();

        UserCredentialsDTO dto = new UserCredentialsDTO("admin@gmail.com", "NekaSifra123");
        HttpEntity<UserCredentialsDTO> requestLogin = new HttpEntity<>(dto, headersAdmin);
        ResponseEntity<AuthTokenDTO> token = restTemplate.exchange(serverPath + "/user/login", HttpMethod.POST, requestLogin, AuthTokenDTO.class);
        headersAdmin.add("Authorization", "Bearer " + token.getBody().getAccessToken());
    }

    // Endpoint createRide
    @Test
    public void createRide_whenPassengerHasRole_returnsRide() {
        setUpPassenger();

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
        setUpPassenger();

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
        setUpDriver();

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

//    @Test
//    public void createRide_whenNoActiveDriver_returnsBadRequest() {
//        setUpPassenger();
//
//        LocationDTO startLocation = new LocationDTO("Bulevar Cara Lazara 90", 45.2405129, 19.8265563);
//        LocationDTO endLocation = new LocationDTO("Bulevar Patrijaha Pavla 2", 45.23984, 19.82062);
//        PathDTO path = new PathDTO(startLocation, endLocation);
//        Set<PathDTO> paths = new HashSet<>();
//        paths.add(path);
//
//        UserRideDTO passenger = new UserRideDTO();
//        passenger.setId(1);
//        Set<UserRideDTO> passengers = new HashSet<>();
//        passengers.add(passenger);
//
//        CreateRideDTO rideDTO = new CreateRideDTO(
//                paths,
//                passengers,
//                VehicleCategory.STANDARD,
//                true,
//                true,
//                LocalDateTime.now()
//        );
//
//        HttpEntity<CreateRideDTO> createRideDTO = new HttpEntity<>(rideDTO, headersPassenger);
//        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride", HttpMethod.POST, createRideDTO, String.class);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Driving is not possible!", response.getBody());
//    }

    @Test
    public void createRide_whenPassengerAlreadyHasRide_returnsBadRequest() {
        setUpPassenger();

        restTemplate = new TestRestTemplate();
        headersDriver = new HttpHeaders();

        UserCredentialsDTO dto = new UserCredentialsDTO("sica@gmail.com", "NekaSifra123");
        HttpEntity<UserCredentialsDTO> requestLogin = new HttpEntity<>(dto, headersDriver);
        ResponseEntity<AuthTokenDTO> token = restTemplate.exchange(serverPath + "/user/login", HttpMethod.POST, requestLogin, AuthTokenDTO.class);
        headersDriver.add("Authorization", "Bearer " + token.getBody().getAccessToken());

        WorkHoursStartDTO startDTO = new WorkHoursStartDTO(LocalDateTime.now());
        HttpEntity<WorkHoursStartDTO> requestWorkHours = new HttpEntity<>(startDTO, headersDriver);
        String id = jwtUtil.extractId(token.getBody().getAccessToken());

        restTemplate.exchange(serverPath + "/driver/" + id + "/working-hour", HttpMethod.POST, requestWorkHours, String.class);

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

        setUpDriver();
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride", HttpMethod.POST, createRideDTO, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
