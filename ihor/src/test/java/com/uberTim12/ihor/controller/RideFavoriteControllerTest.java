package com.uberTim12.ihor.controller;

import com.uberTim12.ihor.dto.ride.CreateFavoriteDTO;
import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.ride.FavoriteFullDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.AuthTokenDTO;
import com.uberTim12.ihor.dto.users.UserCredentialsDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.seeders.SeedUtils;
import com.uberTim12.ihor.seeders.Seeder;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.testng.annotations.BeforeMethod;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({ Seeder.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class RideFavoriteControllerTest {


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

        UserCredentialsDTO dto = new UserCredentialsDTO("staja@gmail.com", "NekaSifra123");
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


    // Endpoint createFavorite
    @Test
    public void createFavorite_whenNotLogged_returnsUnauthorized() {
        PathDTO path = new PathDTO();
        LocationDTO departure = new LocationDTO("Bulevar Evrope 1", 10.0, 10.0);
        LocationDTO destination = new LocationDTO("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setDeparture(departure);
        path.setDestination(destination);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(1);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO("", paths, passengers, VehicleCategory.STANDARD, true, true);

        HttpEntity<CreateFavoriteDTO> createRideDTO = new HttpEntity<>(createFavoriteDTO, null);
        ResponseEntity<FavoriteFullDTO> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.POST, createRideDTO, FavoriteFullDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
