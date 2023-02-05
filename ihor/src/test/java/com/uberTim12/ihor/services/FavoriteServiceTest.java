package com.uberTim12.ihor.services;

import com.uberTim12.ihor.dto.ride.CreateFavoriteDTO;
import com.uberTim12.ihor.dto.route.FavoriteRouteForPassengerDTO;
import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.exception.FavoriteRideExceedException;
import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.repository.ride.IFavoriteRepository;
import com.uberTim12.ihor.service.ride.impl.FavoriteService;
import com.uberTim12.ihor.service.route.interfaces.IPathService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    private IFavoriteRepository favoriteRepository;
    @Mock
    private IPassengerService passengerService;
    @Mock
    private IPathService pathService;
    @InjectMocks
    private FavoriteService favoriteService;

    public FavoriteServiceTest(){}

    // Method create
    @Test()
    @DisplayName("Throw EntityPropertyIsNullException when favorite name is empty")
    public void throwExceptionWhenFavoriteNameIsEmpty() {
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
        assertThrows(EntityPropertyIsNullException.class, () ->  favoriteService.create(createFavoriteDTO));

        verify(passengerService, never()).get(any());
        verify(pathService, never()).save(any());
        verify(favoriteRepository, never()).save(any());
    }

    @Test()
    @DisplayName("Throw EntityPropertyIsNullException when favorite name is null")
    public void throwExceptionWhenFavoriteNameIsNull() {
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

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO(null, paths, passengers, VehicleCategory.STANDARD, true, true);
        assertThrows(EntityPropertyIsNullException.class, () ->  favoriteService.create(createFavoriteDTO));

        verify(passengerService, never()).get(any());
        verify(pathService, never()).save(any());
        verify(favoriteRepository, never()).save(any());
    }

    @Test()
    @DisplayName("Throw EntityPropertyIsNullException when paths in favorite are null")
    public void throwExceptionWhenFavoritePathsAreNull() {
        Set<PathDTO> paths = new HashSet<>();

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(1);
        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateFavoriteDTO createFavoriteDTO = new CreateFavoriteDTO("Kuća poso", paths, passengers, VehicleCategory.STANDARD, true, true);
        assertThrows(EntityPropertyIsNullException.class, () ->  favoriteService.create(createFavoriteDTO));

        verify(passengerService, never()).get(any());
        verify(pathService, never()).save(any());
        verify(favoriteRepository, never()).save(any());
    }

    @Test()
    @DisplayName("Throw FavoriteRideExceedException when passenger already has 10 favorites")
    public void throwExceptionWhenPassengerAlreadyHasTenFavorites() {
        PathDTO pathDTO = new PathDTO();
        LocationDTO departure = new LocationDTO("Bulevar Evrope 1", 10.0, 10.0);
        LocationDTO destination = new LocationDTO("Bulevar oslobodjenja 10", 10.0, 10.0);
        pathDTO.setDeparture(departure);
        pathDTO.setDestination(destination);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(pathDTO);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(1);

        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateFavoriteDTO createFavoriteDTO =
                new CreateFavoriteDTO("Kuća poso", paths, passengers, VehicleCategory.STANDARD, true, true);

        Passenger setPassenger = new Passenger();
        setPassenger.setId(1);
        setPassenger.setFavoriteRoutes(new HashSet<>());

        // set to have 10 favorites
        for (int i=0; i<10;i++){
            Favorite favorite = new Favorite();
            setPassenger.getFavoriteRoutes().add(favorite);
        }

        Mockito.when(passengerService.get(1)).thenReturn(setPassenger);

        assertThrows(FavoriteRideExceedException.class, () ->  favoriteService.create(createFavoriteDTO));

        verify(passengerService, times(1)).get(1);
        verify(favoriteRepository, never()).save(any());
        verify(pathService, never()).save(any());
    }


    @Test()
    @DisplayName("Should return created favorite when passenger can add favorite data to his favorite list")
    public void shouldReturnCreatedFavoriteWhenPassengerCanAddFavorite() throws FavoriteRideExceedException {
        PathDTO pathDTO = new PathDTO();
        LocationDTO departure = new LocationDTO("Bulevar Evrope 1", 10.0, 10.0);
        LocationDTO destination = new LocationDTO("Bulevar oslobodjenja 10", 10.0, 10.0);
        pathDTO.setDeparture(departure);
        pathDTO.setDestination(destination);
        Set<PathDTO> paths = new HashSet<>();
        paths.add(pathDTO);

        UserRideDTO passenger = new UserRideDTO();
        passenger.setId(1);

        Set<UserRideDTO> passengers = new HashSet<>();
        passengers.add(passenger);

        CreateFavoriteDTO createFavoriteDTO =
                new CreateFavoriteDTO("Kuća poso", paths, passengers, VehicleCategory.STANDARD, true, true);

        Passenger setPassenger = new Passenger();
        setPassenger.setId(1);
        setPassenger.setFavoriteRoutes(new HashSet<>());
        Set<Passenger> setPassengers = new HashSet<>();
        setPassengers.add(setPassenger);

        // set to have 9 favorites
        for (int i=0; i<9;i++){
            Favorite favorite = new Favorite();
            setPassenger.getFavoriteRoutes().add(favorite);
        }

        Path path = new Path(1, departure.generateLocation(), destination.generateLocation(), 230.0);
        Set<Path> setPaths = new HashSet<>();
        setPaths.add(path);

        Favorite setFavorite =
                new Favorite(1, "Kuća poso", setPaths, setPassengers, VehicleCategory.STANDARD, true, true);

        Mockito.when(passengerService.get(1)).thenReturn(setPassenger);
        Mockito.when(pathService.save(any(Path.class))).thenReturn(path);
        Mockito.when(favoriteRepository.save(any(Favorite.class))).thenReturn(setFavorite);

        Favorite favorite = favoriteService.create(createFavoriteDTO);

        Assertions.assertThat(favorite.getFavoriteName()).isEqualTo(createFavoriteDTO.getFavoriteName());
        Assertions.assertThat(favorite.getPaths().stream().iterator().next().getEndPoint().getAddress())
                .isEqualTo(createFavoriteDTO.getLocations().iterator().next().getDestination().getAddress());
        Assertions.assertThat(favorite.getPassengers().stream().iterator().next().getId())
                .isEqualTo(createFavoriteDTO.getPassengers().stream().iterator().next().getId());

        verify(passengerService, times(2)).get(1);
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
        verify(pathService, times(1)).save(any(Path.class));
    }

    // Method getForPassenger
    @Test()
    @DisplayName("Throw EntityNotFoundException when id is null")
    public void throwExceptionWhenIdIsNull() {
        assertThrows(EntityNotFoundException.class, () ->  favoriteService.getForPassenger(null));

        verify(passengerService, times(1)).findByIdWithFavorites(null);
    }

    @Test
    @DisplayName("Should return list of favorite rides when passenger exists in the database")
    public void shouldReturnListOfFavoritesWhenPassengerExists() {
        Path path = new Path();
        Location startPoint = new Location("Bulevar Evrope 1", 10.0, 10.0);
        Location endPoint = new Location("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setStartPoint(startPoint);
        path.setEndPoint(endPoint);
        Set<Path> paths = new HashSet<>();
        paths.add(path);

        Passenger passenger = new Passenger();
        passenger.setId(1);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);

        Favorite favorite = new Favorite(1, "Kuća Poso", paths, passengers, VehicleCategory.STANDARD, true, true);
        Set<Favorite> favorites = new HashSet<>();
        favorites.add(favorite);

        passenger.setFavoriteRoutes(favorites);

        Mockito.when(passengerService.findByIdWithFavorites(1)).thenReturn(passenger);

        List<Favorite> favoritesResult = favoriteService.getForPassenger(1);
        Assertions.assertThat(favoritesResult.get(0).getId()).isEqualTo(favorite.getId());
        Assertions.assertThat(favoritesResult.get(0).getFavoriteName()).isEqualTo(favorite.getFavoriteName());
        assertTrue(favoritesResult.get(0).getPassengers().contains(passenger));

        verify(passengerService, times(1)).findByIdWithFavorites(1);
    }

    @Test
    @DisplayName("Throw EntityNotFoundException when passenger not exists in the database")
    public void throwExceptionWhenPassengerNotExists() {
        Mockito.when(passengerService.findByIdWithFavorites(-1)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () ->  favoriteService.getForPassenger(-1));
        verify(passengerService, times(1)).findByIdWithFavorites(-1);
    }

    @Test
    @DisplayName("Should return empty list when passenger exists but does not have favorite rides")
    public void shouldReturnEmptyListWhenPassengerExistsWithoutFavorites() {
        Passenger passenger = new Passenger();
        passenger.setId(12);

        passenger.setFavoriteRoutes(new HashSet<>());

        Mockito.when(passengerService.findByIdWithFavorites(12)).thenReturn(passenger);

        List<Favorite> favoritesResult = favoriteService.getForPassenger(12);
        assertTrue(favoritesResult.isEmpty());

        verify(passengerService, times(1)).findByIdWithFavorites(12);
    }

    @Test
    @DisplayName("Should return list with multiple favorite rides when passenger exists with more favorites")
    public void shouldReturnListWithMultipleFavoritesWhenPassengerHasMoreFavorites() {
        Path path = new Path();
        Location startPoint = new Location("Bulevar Evrope 1", 10.0, 10.0);
        Location endPoint = new Location("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setStartPoint(startPoint);
        path.setEndPoint(endPoint);
        Set<Path> paths = new HashSet<>();
        paths.add(path);

        Passenger passenger = new Passenger();
        passenger.setId(12);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);

        Favorite firstFavorite = new Favorite(1, "Kuća Poso", paths, passengers, VehicleCategory.STANDARD, true, true);
        Favorite secondFavorite = new Favorite(2, "Subota izlazak", paths, passengers, VehicleCategory.STANDARD, true, true);
        Set<Favorite> favorites = new HashSet<>();
        favorites.add(firstFavorite);
        favorites.add(secondFavorite);

        passenger.setFavoriteRoutes(favorites);

        Mockito.when(passengerService.findByIdWithFavorites(12)).thenReturn(passenger);

        List<Favorite> favoritesResult = favoriteService.getForPassenger(12);
        Assertions.assertThat(favoritesResult.get(0).getId()).isEqualTo(firstFavorite.getId());
        Assertions.assertThat(favoritesResult.get(0).getFavoriteName()).isEqualTo(firstFavorite.getFavoriteName());
        Assertions.assertThat(favoritesResult.get(1).getId()).isEqualTo(secondFavorite.getId());
        Assertions.assertThat(favoritesResult.get(1).getFavoriteName()).isEqualTo(secondFavorite.getFavoriteName());
        Assertions.assertThat(favoritesResult.get(0).getPassengers()).isEqualTo(favoritesResult.get(1).getPassengers());
        assertTrue(favoritesResult.get(0).getPassengers().contains(passenger));

        verify(passengerService, times(1)).findByIdWithFavorites(12);
    }

    // Method isFavoriteRouteForPassenger
    @Test()
    @DisplayName("Throw EntityNotFoundException when passenger with passengerId does not exists")
    public void throwExceptionWhenPassengerWithIdNotExists() {
        Mockito.when(passengerService.get(1)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () ->  favoriteService.isFavoriteRouteForPassenger("Mikina 1", "Perina 2", 1));

        verify(passengerService, times(1)).get(1);
        verify(favoriteRepository, never()).findAllForPassengers(any());
    }

    @Test()
    @DisplayName("Should return favoriteRouteForPassengerDTO with false when passenger does not have favorites")
    public void shouldReturnFavoriteRouteForPassengerDTOWithFalseWhenPassengerNotHaveFavorites() {
        Passenger passenger = new Passenger();
        passenger.setId(12);

        Mockito.when(passengerService.get(12)).thenReturn(passenger);
        Mockito.when(favoriteRepository.findAllForPassengers(passenger)).thenReturn(new ArrayList<>());

        FavoriteRouteForPassengerDTO favoriteRouteForPassengerDTO = favoriteService.isFavoriteRouteForPassenger("Mikina 1", "Perina 2", 12);

        assertFalse(favoriteRouteForPassengerDTO.isFavorite());
        Assertions.assertThat(favoriteRouteForPassengerDTO.getFavoriteId()).isEqualTo(0);

        verify(passengerService, times(1)).get(12);
        verify(favoriteRepository, times(1)).findAllForPassengers(passenger);
    }

    @Test()
    @DisplayName("Should return valid favoriteRouteForPassengerDTO when passenger has favorite with that destination and departure")
    public void shouldReturnValidFavoriteRouteForPassengerDTOWhenPassengerHasSpecificFavorite() {
        Passenger passenger = new Passenger();
        passenger.setId(12);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);

        Path path = new Path();
        Location startPoint = new Location("Mikina 1", 10.0, 10.0);
        Location endPoint = new Location("Perina 2", 10.0, 10.0);
        path.setStartPoint(startPoint);
        path.setEndPoint(endPoint);
        Set<Path> paths = new HashSet<>();
        paths.add(path);

        Location startPointSecond = new Location("Mikina 144", 10.0, 10.0);
        Path secondPath = new Path();
        secondPath.setStartPoint(startPointSecond);
        secondPath.setEndPoint(endPoint);
        Set<Path> secondPaths = new HashSet<>();
        secondPaths.add(secondPath);

        Favorite firstFavorite = new Favorite(1, "Kuća Poso", secondPaths, passengers, VehicleCategory.STANDARD, true, true);
        Favorite secondFavorite = new Favorite(2, "Mikina - perina", paths, passengers, VehicleCategory.STANDARD, true, true);
        List<Favorite> favorites = new ArrayList<>();
        favorites.add(firstFavorite);
        favorites.add(secondFavorite);

        Mockito.when(passengerService.get(12)).thenReturn(passenger);
        Mockito.when(favoriteRepository.findAllForPassengers(passenger)).thenReturn(favorites);

        FavoriteRouteForPassengerDTO favoriteRouteForPassengerDTO = favoriteService.isFavoriteRouteForPassenger("Mikina 1", "Perina 2", 12);

        assertTrue(favoriteRouteForPassengerDTO.isFavorite());
        Assertions.assertThat(favoriteRouteForPassengerDTO.getFavoriteId()).isEqualTo(2);

        verify(passengerService, times(1)).get(12);
        verify(favoriteRepository, times(1)).findAllForPassengers(passenger);
    }

    @Test()
    @DisplayName("Should return favoriteRouteForPassengerDTO with false when passenger does not have favorites with that destination and departure")
    public void shouldReturnFavoriteRouteForPassengerDTOWithFalseWhenPassengerHasNotSpecificFavorite() {
        Passenger passenger = new Passenger();
        passenger.setId(12);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);

        Path path = new Path();
        Location startPoint = new Location("Mikina 1", 10.0, 10.0);
        Location endPoint = new Location("Perina 2", 10.0, 10.0);
        path.setStartPoint(startPoint);
        path.setEndPoint(endPoint);
        Set<Path> paths = new HashSet<>();
        paths.add(path);

        Location startPointSecond = new Location("Mikina 144", 10.0, 10.0);
        Path secondPath = new Path();
        secondPath.setStartPoint(startPointSecond);
        secondPath.setEndPoint(endPoint);
        Set<Path> secondPaths = new HashSet<>();
        secondPaths.add(secondPath);

        Favorite firstFavorite = new Favorite(1, "Kuća Poso", secondPaths, passengers, VehicleCategory.STANDARD, true, true);
        Favorite secondFavorite = new Favorite(2, "Mikina - perina", paths, passengers, VehicleCategory.STANDARD, true, true);
        List<Favorite> favorites = new ArrayList<>();
        favorites.add(firstFavorite);
        favorites.add(secondFavorite);

        Mockito.when(passengerService.get(12)).thenReturn(passenger);
        Mockito.when(favoriteRepository.findAllForPassengers(passenger)).thenReturn(favorites);

        FavoriteRouteForPassengerDTO favoriteRouteForPassengerDTO = favoriteService.isFavoriteRouteForPassenger("Mikina 120", "Perina 2", 12);

        assertFalse(favoriteRouteForPassengerDTO.isFavorite());
        Assertions.assertThat(favoriteRouteForPassengerDTO.getFavoriteId()).isEqualTo(0);

        verify(passengerService, times(1)).get(12);
        verify(favoriteRepository, times(1)).findAllForPassengers(passenger);
    }

}
