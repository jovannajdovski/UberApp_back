package com.uberTim12.ihor.controller;

import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.AuthTokenDTO;
import com.uberTim12.ihor.dto.users.UserCredentialsDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.seeders.SeedUtils;
import com.uberTim12.ihor.seeders.Seeder;
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
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith({ Seeder.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class UserRideControllerTest {
    @Autowired
    private SeedUtils seedUtils;
    @Value("${server.port}")
    private int serverPort;

    private final String serverPath = "http://localhost:8081/api";

    @Autowired
    private TestRestTemplate restTemplate;

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

        UserCredentialsDTO dto = new UserCredentialsDTO(email,password);
        HttpEntity<UserCredentialsDTO> requestLogin = new HttpEntity<>(dto, headersAdmin);
        ResponseEntity<AuthTokenDTO> token = restTemplate.exchange(serverPath + "/user/login", HttpMethod.POST, requestLogin, AuthTokenDTO.class);
        headersAdmin.add("Authorization", "Bearer " + token.getBody().getAccessToken());
    }

    // Endpoint getActiveRideForDriver
    @Test
    public void getActiveRide_whenIdPathVariableMismatchType_returnsBadRequest() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        String invalidDriverId="aa";

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+invalidDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void getActiveRide_whenAnotherDriverIsLogged_returnsNotFound() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int invalidDriverId=Seeder.DRIVER_SECOND_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+invalidDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Active ride does not exist!", response.getBody());
    }
    @Test
    public void getActiveRide_whenDriverNotExist_returnsNotFound() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);
        int invalidDriverId=4000;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+invalidDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Driver does not exist!", response.getBody());
    }
    @Test
    public void getActiveRide_whenDriverHasNotActiveRide_returnsNotFound() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+validDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Active ride does not exist!", response.getBody());
    }
    @Test
    public void getActiveRide_whenDriverHasNotActiveRide_returnsForbidden() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+validDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    @Test
    public void getActiveRide_whenNotLoggedUser_returnsUnauthorized() {
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,null);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+validDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void getActiveRide_whenValidDriverIsLogged_returnsBadRide() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;
        
        int rideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,validDriverId,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(rideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,rideId);

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/driver/"+validDriverId+"/active", HttpMethod.GET, httpEntity, RideFullDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideId, response.getBody().getId());
        assertEquals(validDriverId,response.getBody().getDriver().getId());
        assertEquals(RideStatus.STARTED,response.getBody().getStatus());
        assertFalse(response.getBody().isBabyTransport());
        assertFalse(response.getBody().isPetTransport());
        assertEquals(response.getBody().getPassengers().iterator().next().getId(),Seeder.PASSENGER_FIRST_ID);
    }
}
