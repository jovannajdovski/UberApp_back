package com.uberTim12.ihor.controller;

import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.communication.PanicDTO;
import com.uberTim12.ihor.dto.communication.ReasonDTO;
import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.ride.RideIdListDTO;
import com.uberTim12.ihor.dto.ride.RideNoStatusDTO;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    public void getActiveRideForDriver_whenIdPathVariableMismatchType_returnsBadRequest() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        String invalidDriverId="aa";

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+invalidDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void getActiveRideForDriver_whenAnotherDriverIsLogged_returnsNotFound() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int invalidDriverId=Seeder.DRIVER_SECOND_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+invalidDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Active ride does not exist!", response.getBody());
    }
    @Test
    public void getActiveRideForDriver_whenDriverNotExist_returnsNotFound() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);
        int invalidDriverId=4000;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+invalidDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Driver does not exist!", response.getBody());
    }
    @Test
    public void getActiveRideForDriver_whenDriverHasNotActiveRide_returnsNotFound() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+validDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Active ride does not exist!", response.getBody());
    }
    @Test
    public void getActiveRideForDriver_whenPassengerIsLogged_returnsForbidden() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+validDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    @Test
    public void getActiveRideForDriver_whenNotLoggedUser_returnsUnauthorized() {
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,null);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+validDriverId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void getActiveRideForDriver_whenValidDriverIsLogged_returnsRide() {
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
    // Endpoint getAcceptedRidesForDriver
    @Test
    public void getAcceptedRidesForDriver_whenIdPathVariableMismatchType_returnsBadRequest() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        String invalidDriverId="aa";

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+invalidDriverId+"/accepted", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void getAcceptedRidesForDriver_whenAnotherDriverIsLogged_returnsNotFound() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int invalidDriverId=Seeder.DRIVER_SECOND_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+invalidDriverId+"/accepted", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Accepted rides don't exist!", response.getBody());
    }
    @Test
    public void getAcceptedRidesForDriver_whenDriverNotExist_returnsNotFound() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);
        int invalidDriverId=4000;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+invalidDriverId+"/accepted", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Driver does not exist!", response.getBody());
    }
    @Test
    public void getAcceptedRidesForDriver_whenDriverHasNotAcceptedRides_returnsEmptyDTO() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<ObjectListResponseDTO<RideNoStatusDTO>> response = restTemplate.exchange(serverPath + "/ride/driver/" + validDriverId + "/accepted", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0,response.getBody().getTotalCount());
        assertEquals(0,response.getBody().getResults().size());
    }
    @Test
    public void getAcceptedRidesForDriver_whenPassengerIsLogged_returnsForbidden() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+validDriverId+"/accepted", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    @Test
    public void getAcceptedRidesForDriver_whenNotLoggedUser_returnsUnauthorized() {
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,null);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/driver/"+validDriverId+"/accepted", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void getAcceptedRidesForDriver_whenValidDriverIsLogged_returnsRides() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        int firstRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,validDriverId,10d, RideStatus.ACCEPTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(firstRideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,firstRideId);

        int secondRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,validDriverId,10d, RideStatus.ACCEPTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(secondRideId,Seeder.PATH_SECOND_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_SECOND_ID,secondRideId);

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<ObjectListResponseDTO<RideNoStatusDTO>> response = restTemplate.exchange(serverPath + "/ride/driver/" + validDriverId + "/accepted", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {});


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getTotalCount());
        RideNoStatusDTO ride1=response.getBody().getResults().get(0);
        RideNoStatusDTO ride2=response.getBody().getResults().get(1);
        assertEquals(firstRideId, ride1.getId());
        assertEquals(secondRideId, ride2.getId());
        assertEquals(validDriverId,ride1.getDriver().getId());
        assertEquals(validDriverId,ride2.getDriver().getId());
        assertFalse(ride1.isBabyTransport());
        assertFalse(ride1.isPetTransport());
        assertFalse(ride2.isBabyTransport());
        assertFalse(ride2.isPetTransport());
        assertEquals(Seeder.PASSENGER_FIRST_ID,ride1.getPassengers().iterator().next().getId());
        assertEquals(Seeder.PASSENGER_SECOND_ID,ride2.getPassengers().iterator().next().getId());
    }
    // Endpoint getActiveRideForPassenger
    @Test
    public void getActiveRideForPassenger_whenIdPathVariableMismatchType_returnsBadRequest() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        String invalidPassengerId="aa";

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/passenger/"+invalidPassengerId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void getActiveRideForPassenger_whenAnotherDriverIsLogged_returnsNotFound() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        int invalidPassengerId=Seeder.PASSENGER_SECOND_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/passenger/"+invalidPassengerId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Active ride does not exist!", response.getBody());
    }
    @Test
    public void getActiveRideForPassenger_whenDriverNotExist_returnsNotFound() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);
        int invalidPassengerId=4000;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/passenger/"+invalidPassengerId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Passenger does not exist!", response.getBody());
    }
    @Test
    public void getActiveRideForPassenger_whenDriverHasNotActiveRide_returnsNotFound() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        int validPassengerId=Seeder.PASSENGER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/passenger/"+validPassengerId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Active ride does not exist!", response.getBody());
    }
    @Test
    public void getActiveRideForPassenger_whenDriverIsLogged_returnsForbidden() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validPassengerId=Seeder.PASSENGER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/passenger/"+validPassengerId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    @Test
    public void getActiveRideForPassenger_whenNotLoggedUser_returnsUnauthorized() {
        int validPassengerId=Seeder.PASSENGER_FIRST_ID;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,null);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/passenger/"+validPassengerId+"/active", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void getActiveRideForPassenger_whenValidDriverIsLogged_returnsRide() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);
        int validPassengerId=Seeder.PASSENGER_FIRST_ID;

        int rideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(rideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(validPassengerId,rideId);

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersPassenger);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/passenger/"+validPassengerId+"/active", HttpMethod.GET, httpEntity, RideFullDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideId, response.getBody().getId());
        assertEquals(Seeder.DRIVER_FIRST_ID,response.getBody().getDriver().getId());
        assertEquals(RideStatus.STARTED,response.getBody().getStatus());
        assertFalse(response.getBody().isBabyTransport());
        assertFalse(response.getBody().isPetTransport());
        assertEquals(response.getBody().getPassengers().iterator().next().getId(),validPassengerId);
    }

    // Endpoint getRideById
    @Test
    public void getRideById_whenIdPathVariableMismatchType_returnsBadRequest() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        String invalidRideId="aa";

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+invalidRideId, HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void getRideById_whenAnotherDriverIsLogged_returnsNotFound() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);

        int firstRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(firstRideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,firstRideId);

        int secondRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_SECOND_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(secondRideId,Seeder.PATH_SECOND_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,secondRideId);


        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+secondRideId, HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }
    @Test
    public void getRideById_whenAnotherPassengerIsLogged_returnsNotFound() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int firstRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(firstRideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,firstRideId);

        int secondRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(secondRideId,Seeder.PATH_SECOND_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_SECOND_ID,secondRideId);


        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+secondRideId, HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }
    @Test
    public void getRideById_whenRideNotExist_returnsNotFound() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);
        int invalidRideId=4000;

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+invalidRideId, HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }
    @Test
    public void getRideById_whenNotLoggedUser_returnsUnauthorized() {

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,null);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+1, HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void getRideById_whenValidDriverIsLogged_returnsRide() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        int rideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,validDriverId,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(rideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,rideId);

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersDriver);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/"+rideId, HttpMethod.GET, httpEntity, RideFullDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideId, response.getBody().getId());
        assertEquals(validDriverId,response.getBody().getDriver().getId());
        assertEquals(RideStatus.STARTED,response.getBody().getStatus());
        assertFalse(response.getBody().isBabyTransport());
        assertFalse(response.getBody().isPetTransport());
        assertEquals(response.getBody().getPassengers().iterator().next().getId(),Seeder.PASSENGER_FIRST_ID);
    }
    @Test
    public void getRideById_whenValidPassengerIsLogged_returnsRide() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int rideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(rideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,rideId);

        HttpEntity<?> httpEntity = new HttpEntity<>(null ,headersPassenger);
        ResponseEntity<RideFullDTO> response = restTemplate.exchange(serverPath + "/ride/"+rideId, HttpMethod.GET, httpEntity, RideFullDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideId, response.getBody().getId());
        assertEquals(Seeder.DRIVER_FIRST_ID,response.getBody().getDriver().getId());
        assertEquals(RideStatus.STARTED,response.getBody().getStatus());
        assertFalse(response.getBody().isBabyTransport());
        assertFalse(response.getBody().isPetTransport());
        assertEquals(response.getBody().getPassengers().iterator().next().getId(),Seeder.PASSENGER_FIRST_ID);
    }

    // Endpoint getRidesById
    @Test
    public void getRidesById_whenInvalidRideIdListDTO_returnsBadRequest() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        List<Integer> ids=new ArrayList<>();
        ids.add(-5);
        ids.add(-3);
        RideIdListDTO rideIdListDTO=new RideIdListDTO(ids);

        HttpEntity<?> httpEntity = new HttpEntity<>(rideIdListDTO ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/specific-rides", HttpMethod.PUT, httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
   @Test
    public void getRidesById_whenRideNotExist_returnsNotFound() {
       setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
       List<Integer> ids=new ArrayList<>();
       ids.add(5);
       RideIdListDTO rideIdListDTO=new RideIdListDTO(ids);

       HttpEntity<RideIdListDTO> httpEntity = new HttpEntity<>(rideIdListDTO ,headersDriver);
       ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/specific-rides", HttpMethod.PUT, httpEntity, String.class);
       assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
       assertEquals("Ride does not exist!", response.getBody());
    }
    @Test
    public void getRidesById_whenNotLoggedUser_returnsUnauthorized() {

        int rideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(rideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,rideId);
        List<Integer> ids=new ArrayList<>();
        ids.add(rideId);
        RideIdListDTO rideIdListDTO=new RideIdListDTO(ids);
        HttpEntity<?> httpEntity = new HttpEntity<>(rideIdListDTO ,null);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/specific-rides", HttpMethod.PUT, httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void getRidesById_whenValidUserIsLoggedAndEmptyBodyDTO_returnsEmptyList() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        int firstRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,validDriverId,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(firstRideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,firstRideId);

        int secondRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_SECOND_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(secondRideId,Seeder.PATH_SECOND_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_SECOND_ID,secondRideId);

        List<Integer> ids=new ArrayList<>();
        ids.add(firstRideId);
        ids.add(secondRideId);
        RideIdListDTO rideIdListDTO=new RideIdListDTO(ids);

        HttpEntity<RideIdListDTO> httpEntity = new HttpEntity<>(rideIdListDTO ,headersDriver);
        ResponseEntity<ObjectListResponseDTO<RideFullDTO>> response = restTemplate.exchange(serverPath + "/ride/specific-rides", HttpMethod.PUT, httpEntity, new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getTotalCount());
        RideFullDTO ride1=response.getBody().getResults().get(0);
        RideFullDTO ride2=response.getBody().getResults().get(1);
        assertEquals(firstRideId, ride1.getId());
        assertEquals(secondRideId, ride2.getId());
        assertEquals(validDriverId,ride1.getDriver().getId());
        assertEquals(Seeder.DRIVER_SECOND_ID,ride2.getDriver().getId());
        assertFalse(ride1.isBabyTransport());
        assertFalse(ride1.isPetTransport());
        assertFalse(ride2.isBabyTransport());
        assertFalse(ride2.isPetTransport());
        assertEquals(Seeder.PASSENGER_FIRST_ID,ride1.getPassengers().iterator().next().getId());
        assertEquals(Seeder.PASSENGER_SECOND_ID,ride2.getPassengers().iterator().next().getId());
    }
    @Test
    public void getRidesById_whenValidUserIsLogged_returnsRide() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        int firstRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,validDriverId,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(firstRideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,firstRideId);

        int secondRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_SECOND_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(secondRideId,Seeder.PATH_SECOND_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_SECOND_ID,secondRideId);

        RideIdListDTO rideIdListDTO=new RideIdListDTO(new ArrayList<>());

        HttpEntity<RideIdListDTO> httpEntity = new HttpEntity<>(rideIdListDTO ,headersDriver);
        ResponseEntity<ObjectListResponseDTO<RideFullDTO>> response = restTemplate.exchange(serverPath + "/ride/specific-rides", HttpMethod.PUT, httpEntity, new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getTotalCount());
        assertEquals(0,response.getBody().getResults().size());
    }

    // Endpoint panicRide
    @Test
    public void addPanic_whenIdPathVariableMismatchType_returnsBadRequest() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        String invalidRideId="aaa";
        ReasonDTO reasonDTO=new ReasonDTO("reason");

        HttpEntity<ReasonDTO> httpEntity = new HttpEntity<>(reasonDTO ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+invalidRideId+"/panic", HttpMethod.PUT, httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void addPanic_whenReasonDTONotValid_returnsBadRequest() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        ReasonDTO reasonDTO=new ReasonDTO("");

        HttpEntity<ReasonDTO> httpEntity = new HttpEntity<>(reasonDTO ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+1+"/panic", HttpMethod.PUT, httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void addPanicByAdmin_whenURLIsWrong_returnsBadRequest() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);
        int firstRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(firstRideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,firstRideId);

        ReasonDTO reasonDTO=new ReasonDTO("reason");
        HttpEntity<ReasonDTO> httpEntity = new HttpEntity<>(reasonDTO ,headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+firstRideId+"/paniccc", HttpMethod.PUT, httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void addPanicByAdmin_whenHTTPMethodIsWrong_returnsBadRequest() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);
        int firstRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(firstRideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,firstRideId);

        ReasonDTO reasonDTO=new ReasonDTO("reason");
        HttpEntity<ReasonDTO> httpEntity = new HttpEntity<>(reasonDTO ,headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+firstRideId+"/panic", HttpMethod.GET, httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void addPanicByDriver_whenAnotherDriverIsLogged_returnsNotFound() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);

        int firstRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(firstRideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,firstRideId);

        int secondRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_SECOND_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(secondRideId,Seeder.PATH_SECOND_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,secondRideId);

        int invalidRideId=secondRideId;
        ReasonDTO reasonDTO=new ReasonDTO("reason");

        HttpEntity<?> httpEntity = new HttpEntity<>(reasonDTO ,headersDriver);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+invalidRideId+"/panic", HttpMethod.PUT, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }
    @Test
    public void addPanicByPassenger_whenAnotherPassengerIsLogged_returnsNotFound() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int firstRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(firstRideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,firstRideId);

        int secondRideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(secondRideId,Seeder.PATH_SECOND_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_SECOND_ID,secondRideId);

        int invalidRideId=secondRideId;
        ReasonDTO reasonDTO=new ReasonDTO("reason");

        HttpEntity<ReasonDTO> httpEntity = new HttpEntity<>(reasonDTO ,headersPassenger);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+invalidRideId+"/panic", HttpMethod.PUT, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }
    @Test
    public void addPanicByAdmin_whenRideNotExist_returnsNotFound() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);
        int invalidRideId=4000;

        ReasonDTO reasonDTO=new ReasonDTO("reason");
        HttpEntity<ReasonDTO> httpEntity = new HttpEntity<>(reasonDTO ,headersAdmin);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+invalidRideId+"/panic", HttpMethod.PUT, httpEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }
    @Test
    public void addPanic_whenNotLoggedUser_returnsUnauthorized() {
        int rideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(rideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,rideId);
        ReasonDTO reasonDTO=new ReasonDTO("reason");
        HttpEntity<ReasonDTO> httpEntity = new HttpEntity<>(reasonDTO ,null);
        ResponseEntity<String> response = restTemplate.exchange(serverPath + "/ride/"+rideId+"/panic", HttpMethod.PUT, httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void addPanicByDriver_whenValidDriverIsLogged_returnsPanicDTO() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);
        int validDriverId=Seeder.DRIVER_FIRST_ID;

        int rideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,validDriverId,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(rideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,rideId);

        ReasonDTO reasonDTO=new ReasonDTO("reason");

        HttpEntity<ReasonDTO> httpEntity = new HttpEntity<>(reasonDTO ,headersDriver);
        ResponseEntity<PanicDTO> response = restTemplate.exchange(serverPath + "/ride/"+rideId+"/panic", HttpMethod.PUT, httpEntity, PanicDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideId,response.getBody().getRide().getId());
        assertEquals(Seeder.DRIVER_FIRST_EMAIL,response.getBody().getUser().getEmail());
        assertEquals("reason",response.getBody().getReason());
    }
    @Test
    public void addPanicByPassenger_whenValidPassengerIsLogged_returnsPanicDTO() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);


        int rideId= seedUtils.insertRide(LocalDateTime.now(),LocalDateTime.now(),1000d,Seeder.DRIVER_FIRST_ID,10d, RideStatus.STARTED,false,false,Seeder.VEHICLETYPE_FIRST_ID,false,LocalDateTime.now());
        seedUtils.addPathToRide(rideId,Seeder.PATH_FIRST_ID);
        seedUtils.addPassengerToRide(Seeder.PASSENGER_FIRST_ID,rideId);

        ReasonDTO reasonDTO=new ReasonDTO("reason1");

        HttpEntity<ReasonDTO> httpEntity = new HttpEntity<>(reasonDTO ,headersPassenger);
        ResponseEntity<PanicDTO> response = restTemplate.exchange(serverPath + "/ride/"+rideId+"/panic", HttpMethod.PUT, httpEntity, PanicDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rideId,response.getBody().getRide().getId());
        assertEquals(Seeder.PASSENGER_FIRST_EMAIL,response.getBody().getUser().getEmail());
        assertEquals("reason1",response.getBody().getReason());
    }


}
