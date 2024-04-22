package com.uberTim12.ihor.controller;

import com.uberTim12.ihor.dto.ResponseMessageDTO;
import com.uberTim12.ihor.dto.ride.CreateFavoriteDTO;
import com.uberTim12.ihor.dto.ride.CreateRideDTO;
import com.uberTim12.ihor.dto.ride.FavoriteFullDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.route.FavoriteRouteForPassengerDTO;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.AuthTokenDTO;
import com.uberTim12.ihor.dto.users.UserCredentialsDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.Favorite;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.BeforeMethod;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO("", paths, passengers,
                VehicleCategory.STANDARD, true, true);

        HttpEntity<CreateFavoriteDTO> createRideDTO = new HttpEntity<>(createFavoriteDTO, null);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.POST, createRideDTO, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void createFavorite_whenDriverTries_returnsForbidden() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);

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

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO("Kuca posao", paths, passengers,
                VehicleCategory.STANDARD, true, true);

        HttpEntity<CreateFavoriteDTO> createRideDTO = new HttpEntity<>(createFavoriteDTO, headersDriver);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.POST, createRideDTO, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void createFavorite_whenInvalidCreateFavoriteRideDTO_returnsBadReq() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

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

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO("", paths, passengers,
                VehicleCategory.STANDARD, true, true);

        HttpEntity<CreateFavoriteDTO> createRideDTO = new HttpEntity<>(createFavoriteDTO, headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.POST, createRideDTO, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void createFavorite_whenCreateFavoriteRideDTOHasInvalidPassengers_returnsNotFound() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        PathDTO path = new PathDTO();
        LocationDTO departure = new LocationDTO("Bulevar Evrope 1", 10.0, 10.0);
        LocationDTO destination = new LocationDTO("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setDeparture(departure);
        path.setDestination(destination);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(120);
        UserRideDTO passenger2 = new UserRideDTO();
        passenger2.setId(Seeder.PASSENGER_FIRST_ID);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);
        passengers.add(passenger2);

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO("Moja voznja", paths, passengers,
                VehicleCategory.STANDARD, true, true);

        HttpEntity<CreateFavoriteDTO> createRideDTO = new HttpEntity<>(createFavoriteDTO, headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.POST, createRideDTO, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Passenger does not exist!", response.getBody());
    }

    @Test
    public void createFavorite_whenPassengerNotInListOfPassengers_returnsNotFound() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        PathDTO path = new PathDTO();
        LocationDTO departure = new LocationDTO("Bulevar Evrope 1", 10.0, 10.0);
        LocationDTO destination = new LocationDTO("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setDeparture(departure);
        path.setDestination(destination);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(Seeder.PASSENGER_SECOND_ID);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO("Moja voznja", paths, passengers,
                VehicleCategory.STANDARD, true, true);

        HttpEntity<CreateFavoriteDTO> createRideDTO = new HttpEntity<>(createFavoriteDTO, headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.POST, createRideDTO, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ride does not exist!", response.getBody());
    }

    public void setUpNineFavorites(){
        int favoriteId1 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_SECOND_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId1);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId1);

        int favoriteId2 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        seedUtils.addPathToFavorite(pathId,favoriteId2);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId2);

        int favoriteId3 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        seedUtils.addPathToFavorite(pathId,favoriteId3);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId3);

        int favoriteId4 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        seedUtils.addPathToFavorite(pathId,favoriteId4);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId4);

        int favoriteId5 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        seedUtils.addPathToFavorite(pathId,favoriteId5);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId5);

        int favoriteId6 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        seedUtils.addPathToFavorite(pathId,favoriteId6);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId6);

        int favoriteId7 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        seedUtils.addPathToFavorite(pathId,favoriteId7);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId7);

        int favoriteId8 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        seedUtils.addPathToFavorite(pathId,favoriteId8);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId8);

        int favoriteId9 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        seedUtils.addPathToFavorite(pathId,favoriteId9);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId9);
    }

    @Test
    public void createFavorite_whenPassengerAlreadyHasTenFavorites_returnsBadReq() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        setUpNineFavorites();

        int favoriteId10 = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_SECOND_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId10);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId10);

        PathDTO path = new PathDTO();
        LocationDTO departure = new LocationDTO("Bulevar Evrope 1", 10.0, 10.0);
        LocationDTO destination = new LocationDTO("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setDeparture(departure);
        path.setDestination(destination);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(Seeder.PASSENGER_FIRST_ID);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO("Moja voznja", paths, passengers,
                VehicleCategory.STANDARD, true, true);

        HttpEntity<CreateFavoriteDTO> createRideDTO = new HttpEntity<>(createFavoriteDTO, headersPassenger);
        ResponseEntity<ResponseMessageDTO> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.POST, createRideDTO, ResponseMessageDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Number of favorite rides cannot exceed 10!", response.getBody().getMessage());
    }

    @Test
    public void createFavorite_whenPassengerEnterValidData_returnsFavorite() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        setUpNineFavorites();

        PathDTO path = new PathDTO();
        LocationDTO departure = new LocationDTO("Bulevar Evrope 1", 10.0, 10.0);
        LocationDTO destination = new LocationDTO("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setDeparture(departure);
        path.setDestination(destination);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(path);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(Seeder.PASSENGER_FIRST_ID);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO("Moja voznja", paths, passengers,
                VehicleCategory.STANDARD, true, true);

        HttpEntity<CreateFavoriteDTO> createRideDTO = new HttpEntity<>(createFavoriteDTO, headersPassenger);
        ResponseEntity<FavoriteFullDTO> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.POST, createRideDTO, FavoriteFullDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(response.getBody().getPassengers().stream().iterator().next().getId(), passenger.getId());
        assertEquals(createFavoriteDTO.getFavoriteName(), response.getBody().getFavoriteName());
        assertEquals(createFavoriteDTO.getVehicleType(), response.getBody().getVehicleType());
    }


    // Endpoint getFavorites
    @Test
    public void getFavorites_whenNotLogged_returnsUnauthorized() {

        HttpEntity<String> entity = new HttpEntity<>(null);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void getFavorites_whenPassengerTries_returnsForbidden() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites", HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void getFavorites_whenNoFavorites_returnsEmptyList() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);

        HttpEntity<String> entity = new HttpEntity<>(headersAdmin);
        ResponseEntity<Set<FavoriteFullDTO>> response = restTemplate.exchange(serverPath + "/ride/favorites",
                HttpMethod.GET, entity, new ParameterizedTypeReference<Set<FavoriteFullDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Set<FavoriteFullDTO> set = response.getBody();
        assertNotNull(set);
        assertTrue(set.isEmpty());
    }

    @Test
    public void getFavorites_whenFavoriteExists_returnsListWithAllFavorites() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_SECOND_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId);

        HttpEntity<String> entity = new HttpEntity<>(headersAdmin);
        ResponseEntity<Set<FavoriteFullDTO>> response = restTemplate.exchange(serverPath + "/ride/favorites",
                HttpMethod.GET, entity, new ParameterizedTypeReference<Set<FavoriteFullDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Set<FavoriteFullDTO> set = response.getBody();
        assertNotNull(set);
        assertFalse(set.isEmpty());
        assertEquals("Kuca poso", set.iterator().next().getFavoriteName());
    }


    // Endpoint getFavoritesForPassenger
    @Test
    public void getFavoritesForPassenger_whenNotLogged_returnsUnauthorized() {

        HttpEntity<String> entity = new HttpEntity<>(null);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/" + Seeder.PASSENGER_FIRST_ID, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void getFavoritesForPassenger_whenDriverTries_returnsForbidden() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);

        HttpEntity<String> entity = new HttpEntity<>(headersDriver);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/" + Seeder.DRIVER_FIRST_ID, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void getFavoritesForPassenger_whenPassengerWantFavoritesForOther_returnsNotFound() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/" + Seeder.PASSENGER_SECOND_ID, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Favorites do not exist!", response.getBody());
    }

    @Test
    public void getFavoritesForPassenger_whenAdminWantFavoritesForPassengerWhoNotExists_returnsNotFound() {
        setUpAdmin(Seeder.ADMIN_EMAIL, Seeder.PASSWORD);

        int wrongPassengerId = 100;

        HttpEntity<String> entity = new HttpEntity<>(headersAdmin);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/" + wrongPassengerId, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Passenger does not exist!", response.getBody());
    }

    @Test
    public void getFavoritesForPassenger_whenNoFavorites_returnsEmptyList() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<List<FavoriteFullDTO>> response = restTemplate.exchange(serverPath + "/ride/favorites/"+ Seeder.PASSENGER_FIRST_ID,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<FavoriteFullDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<FavoriteFullDTO> list = response.getBody();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void getFavoritesForPassenger_whenFavoriteExists_returnsListWithFavorites() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_SECOND_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<List<FavoriteFullDTO>> response = restTemplate.exchange(serverPath + "/ride/favorites/"+ Seeder.PASSENGER_FIRST_ID,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<FavoriteFullDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<FavoriteFullDTO> list = response.getBody();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals("Kuca poso", list.get(0).getFavoriteName());
    }


    // Endpoint isFavoritesForPassenger
    @Test
    public void isFavoritesForPassenger_whenNotLogged_returnsUnauthorized() {

        String url = serverPath + "/ride/favorites/passenger/ride";

        String start = URLEncoder.encode("Berislava berica 5", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("Rumenacka 23", StandardCharsets.UTF_8);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("from", start)
                .queryParam("to", end);

        HttpEntity<String> entity = new HttpEntity<>(null);
        ResponseEntity<?> response = restTemplate.exchange( builder.toUriString(), HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void isFavoritesForPassenger_whenDriverTries_returnsForbidden() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);

        String url = serverPath + "/ride/favorites/passenger/ride";

        String start = URLEncoder.encode("Berislava berica 5", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("Rumenacka 23", StandardCharsets.UTF_8);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("from", start)
                .queryParam("to", end);

        HttpEntity<String> entity = new HttpEntity<>(headersDriver);
        ResponseEntity<?> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void isFavoritesForPassenger_whenPassengerNotHaveFavorites_returnsFalseForIsFavorite() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        String url = serverPath + "/ride/favorites/passenger/ride";

        String start = URLEncoder.encode("Berislava berica 5", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("Rumenacka 23", StandardCharsets.UTF_8);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("from", start)
                .queryParam("to", end);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<FavoriteRouteForPassengerDTO> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, FavoriteRouteForPassengerDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        FavoriteRouteForPassengerDTO isFavoriteDTO = response.getBody();
        assertNotNull(isFavoriteDTO);
        assertFalse(isFavoriteDTO.isFavorite());
        assertEquals(isFavoriteDTO.getFavoriteId(),0);
    }

    @Test
    public void isFavoritesForPassenger_whenPassengerHaveFavoritesButNotThisOne_returnsFalseForIsFavorite() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_SECOND_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId);

        String url = serverPath + "/ride/favorites/passenger/ride";

        String start = URLEncoder.encode("Berislava berica 5", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("Rumenacka 23", StandardCharsets.UTF_8);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("from", start)
                .queryParam("to", end);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<FavoriteRouteForPassengerDTO> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, FavoriteRouteForPassengerDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        FavoriteRouteForPassengerDTO isFavoriteDTO = response.getBody();
        assertNotNull(isFavoriteDTO);
        assertFalse(isFavoriteDTO.isFavorite());
        assertEquals(isFavoriteDTO.getFavoriteId(),0);
    }

    @Test
    public void isFavoritesForPassenger_whenPassengerHaveOnlyThisFavorite_returnsTrueAndFavoriteId() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_THIRD_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId);

        String url = serverPath + "/ride/favorites/passenger/ride";

        String start = URLEncoder.encode("Berislava berica 5", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("Rumenacka 23", StandardCharsets.UTF_8);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("from", start)
                .queryParam("to", end);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<FavoriteRouteForPassengerDTO> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, FavoriteRouteForPassengerDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        FavoriteRouteForPassengerDTO isFavoriteDTO = response.getBody();
        assertNotNull(isFavoriteDTO);
        assertTrue(isFavoriteDTO.isFavorite());
        assertEquals(isFavoriteDTO.getFavoriteId(),favoriteId);
    }

    @Test
    public void isFavoritesForPassenger_whenPassengerHaveFavoriteWithMultipleOthers_returnsTrueAndFavoriteId() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int favoriteId2 = seedUtils.insertFavorite("Moja omiljena",true, true, 1);
        int pathId2 = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_SECOND_ID, 333);
        seedUtils.addPathToFavorite(pathId2,favoriteId2);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId2);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_THIRD_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId);

        int favoriteId3 = seedUtils.insertFavorite("Panika",true, true, 1);
        int pathId3 = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_THIRD_ID, 444);
        seedUtils.addPathToFavorite(pathId3,favoriteId3);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId3);

        String url = serverPath + "/ride/favorites/passenger/ride";

        String start = URLEncoder.encode("Berislava berica 5", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("Rumenacka 23", StandardCharsets.UTF_8);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("from", start)
                .queryParam("to", end);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<FavoriteRouteForPassengerDTO> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, FavoriteRouteForPassengerDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        FavoriteRouteForPassengerDTO isFavoriteDTO = response.getBody();
        assertNotNull(isFavoriteDTO);
        assertTrue(isFavoriteDTO.isFavorite());
        assertEquals(isFavoriteDTO.getFavoriteId(),favoriteId);
    }


    //deleteFavorite
    @Test
    public void deleteFavorite_whenNotLogged_returnsUnauthorized() {

        int randomFavoriteId = 11;
        HttpEntity<String> entity = new HttpEntity<>(null);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/" + randomFavoriteId, HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void deleteFavorite_whenDriverTries_returnsForbidden() {
        setUpDriver(Seeder.DRIVER_FIRST_EMAIL, Seeder.PASSWORD);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_THIRD_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId);

        HttpEntity<String> entity = new HttpEntity<>(headersDriver);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/" + favoriteId, HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void deleteFavorite_whenPassengerWantToDeleteOtherFavorite_returnsNotFound() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_THIRD_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_SECOND_ID,favoriteId);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/" + favoriteId, HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Favorite location does not exist!", response.getBody());
    }

    @Test
    public void deleteFavorite_whenPassengerWantToDeleteNotExistingFavorite_returnsNotFound() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_THIRD_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId);

        int wrongPassengerId = 100;

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/" + wrongPassengerId, HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Favorite location does not exist!", response.getBody());
    }

    @Test
    public void deleteFavorite_whenPassengerSuccessfullyDeleteFavorite_returnsSuccessfulDeletionMessage() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_THIRD_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/"+ favoriteId,
                HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void deleteFavorite_whenPassengerSuccessfullyDeleteFavoriteFromMultipleFavorites_returnsSuccessfulDeletionMessage() {
        setUpPassenger(Seeder.PASSENGER_FIRST_EMAIL, Seeder.PASSWORD);

        int favoriteId2 = seedUtils.insertFavorite("Moja omiljena",true, true, 1);
        int pathId2 = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_SECOND_ID, 333);
        seedUtils.addPathToFavorite(pathId2,favoriteId2);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId2);

        int favoriteId = seedUtils.insertFavorite("Kuca poso",true, true, 1);
        int pathId = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_THIRD_ID, 100);
        seedUtils.addPathToFavorite(pathId,favoriteId);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId);

        int favoriteId3 = seedUtils.insertFavorite("Panika",true, true, 1);
        int pathId3 = seedUtils.insertPath(Seeder.LOCATION_FIRST_ID,Seeder.LOCATION_THIRD_ID, 444);
        seedUtils.addPathToFavorite(pathId3,favoriteId3);
        seedUtils.addPassengerToFavorite(Seeder.PASSENGER_FIRST_ID,favoriteId3);

        HttpEntity<String> entity = new HttpEntity<>(headersPassenger);
        ResponseEntity<?> response = restTemplate.exchange(serverPath + "/ride/favorites/"+ favoriteId,
                HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
