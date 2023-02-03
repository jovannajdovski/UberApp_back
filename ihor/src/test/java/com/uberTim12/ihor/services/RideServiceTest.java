package com.uberTim12.ihor.services;

import com.uberTim12.ihor.dto.ride.RideResponseDTO;
import com.uberTim12.ihor.exception.NoActiveRideException;
import com.uberTim12.ihor.exception.RideStatusException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.route.impl.LocationService;
import jakarta.persistence.EntityNotFoundException;
import net.minidev.json.parser.ParseException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {
    @Mock
    private IRideRepository rideRepository;
    @Mock
    private IDriverRepository driverRepository;
    @Mock
    private IPassengerRepository passengerRepository;
    @Mock
    private LocationService locationService;
    @InjectMocks
    private RideService rideService;


    // Method getEstimatedRoute
    @Test
    @DisplayName("Should calculate distance and estimated time for valid ride")
    public void shouldCalculateDistanceAndEstimatedTime() throws IOException, ParseException {
        Ride ride = new Ride();
        Path path = new Path();
        Location startPoint = new Location("Bulevar oslobodjenja 1", 10.0, 10.0);
        Location endPoint = new Location("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setStartPoint(startPoint);
        path.setEndPoint(endPoint);

        Set<Path> paths = new HashSet<>();
        paths.add(path);
        ride.setPaths(paths);

        Mockito.when(locationService.calculateDistance(startPoint, endPoint)).thenReturn(10d);
        Mockito.when(locationService.calculateEstimatedTime(startPoint, endPoint)).thenReturn(10d);

        RideResponseDTO rideResponseDTO = rideService.getEstimatedRoute(ride);
        Assertions.assertThat(rideResponseDTO.getEstimatedTimeInMinutes()).isEqualTo(10d);
        Assertions.assertThat(rideResponseDTO.getEstimatedCost()).isEqualTo(500 + 10d * 120);
    }

    @Test
    @DisplayName("Should should return max for calculate distance error")
    public void shouldReturnMaxForCalculateDistanceError() throws IOException, ParseException {
        Ride ride = new Ride();
        Path path = new Path();
        Location startPoint = new Location("Bulevar oslobodjenja 1", 10.0, 10.0);
        Location endPoint = new Location("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setStartPoint(startPoint);
        path.setEndPoint(endPoint);

        Set<Path> paths = new HashSet<>();
        paths.add(path);
        ride.setPaths(paths);

        Mockito.when(locationService.calculateDistance(startPoint, endPoint)).thenThrow(ParseException.class);
        Mockito.when(locationService.calculateEstimatedTime(startPoint, endPoint)).thenReturn(10d);

        RideResponseDTO rideResponseDTO = rideService.getEstimatedRoute(ride);
        Assertions.assertThat(rideResponseDTO.getEstimatedTimeInMinutes()).isEqualTo(10d);
        Assertions.assertThat(rideResponseDTO.getEstimatedCost()).isInfinite();
    }

    @Test
    @DisplayName("Should should return max for calculate time error")
    public void shouldReturnMaxForCalculateTimeError() throws IOException, ParseException {
        Ride ride = new Ride();
        Path path = new Path();
        Location startPoint = new Location("Bulevar oslobodjenja 1", 10.0, 10.0);
        Location endPoint = new Location("Bulevar oslobodjenja 10", 10.0, 10.0);
        path.setStartPoint(startPoint);
        path.setEndPoint(endPoint);

        Set<Path> paths = new HashSet<>();
        paths.add(path);
        ride.setPaths(paths);

        Mockito.when(locationService.calculateDistance(startPoint, endPoint)).thenReturn(10d);
        Mockito.when(locationService.calculateEstimatedTime(startPoint, endPoint)).thenThrow(ParseException.class);

        RideResponseDTO rideResponseDTO = rideService.getEstimatedRoute(ride);
        Assertions.assertThat(rideResponseDTO.getEstimatedTimeInMinutes()).isEqualTo(Double.MAX_VALUE);
        Assertions.assertThat(rideResponseDTO.getEstimatedCost()).isEqualTo(500 + 10d * 120);
    }

    @Test
    @DisplayName("Should should return max for empty paths")
    public void shouldReturnMaxForEmptyPath() throws IOException, ParseException {
        Ride ride = new Ride();
        Path path = new Path();
        path.setStartPoint(new Location());
        path.setEndPoint(new Location());

        Set<Path> paths = new HashSet<>();
        paths.add(path);
        ride.setPaths(paths);

        Mockito.when(locationService.calculateDistance(any(Location.class), any(Location.class))).thenThrow(ParseException.class);
        Mockito.when(locationService.calculateEstimatedTime(any(Location.class), any(Location.class))).thenThrow(ParseException.class);

        RideResponseDTO rideResponseDTO = rideService.getEstimatedRoute(ride);
        Assertions.assertThat(rideResponseDTO.getEstimatedTimeInMinutes()).isEqualTo(Double.MAX_VALUE);
        Assertions.assertThat(rideResponseDTO.getEstimatedCost()).isInfinite();
    }

    // Method getRides
    //TODO

    //Method findActiveByDriver
    @Test
    @DisplayName("Should return passengers for valid ")
    public void shouldReturnRideWithPassengers() throws NoActiveRideException {
        Driver driver = new Driver();
        Passenger passenger = new Passenger();
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);

        Ride ride = new Ride();
        ride.setId(1);
        ride.setPassengers(passengers);

        List<Ride> rides = new ArrayList<>();
        rides.add(ride);

        Mockito.when(rideRepository.findActiveByDriver(driver, RideStatus.STARTED)).thenReturn(rides);
        Mockito.when(rideService.findPassengersForRide(any(Integer.class))).thenReturn(new ArrayList<>(passengers));
        Ride foundRide = rideService.findActiveByDriver(driver);
        Assertions.assertThat(foundRide.getPassengers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw exception for no active rides")
    public void shouldThrowExceptionForNoActiveRidesDriver() {
        Driver driver = new Driver();
        Passenger passenger = new Passenger();
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);

        Ride ride = new Ride();
        ride.setId(1);
        ride.setPassengers(passengers);

        Mockito.when(rideRepository.findActiveByDriver(driver, RideStatus.STARTED)).thenReturn(new ArrayList<>());
        assertThrows(NoActiveRideException.class, () -> {
            rideService.findActiveByDriver(driver);
        });
    }

    @Test
    @DisplayName("Should throw exception for no active rides")
    public void shouldReturnCorrectPassengersInRide() throws NoActiveRideException {
        Driver driver = new Driver();
        Passenger passenger = new Passenger();
        passenger.setId(1);
        Passenger passenger1 = new Passenger();
        passenger.setId(2);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);
        Set<Passenger> passengers1 = new HashSet<>();
        passengers1.add(passenger1);

        Ride ride = new Ride();
        ride.setId(1);
        ride.setPassengers(passengers);

        Ride ride1 = new Ride();
        ride.setId(2);
        ride.setPassengers(passengers1);

        List<Ride> rides = new ArrayList<>();
        rides.add(ride);
        rides.add(ride1);

        Mockito.when(rideRepository.findActiveByDriver(driver, RideStatus.STARTED)).thenReturn(rides);
        Mockito.when(rideService.findPassengersForRide(2)).thenReturn(new ArrayList<>(passengers));
        Ride foundRide = rideService.findActiveByDriver(driver);
        List<Passenger> foundPassengers = new ArrayList<>(foundRide.getPassengers());
        Assertions.assertThat(foundRide.getId()).isEqualTo(2);
        Assertions.assertThat(foundPassengers.get(0).getId()).isEqualTo(2);
    }

    //Method findActiveByPassenger
    @Test
    @DisplayName("Should return passengers for valid")
    public void shouldReturnRideWithPassengersActivePassenger() throws NoActiveRideException {
        Passenger passengerMain = new Passenger();
        Passenger passenger = new Passenger();
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);

        Ride ride = new Ride();
        ride.setId(1);
        ride.setPassengers(passengers);

        List<Ride> rides = new ArrayList<>();
        rides.add(ride);

        Mockito.when(rideRepository.findActiveByPassenger(passengerMain, RideStatus.STARTED)).thenReturn(rides);
        Mockito.when(rideService.findPassengersForRide(any(Integer.class))).thenReturn(new ArrayList<>(passengers));
        Ride foundRide = rideService.findActiveByPassenger(passengerMain);
        Assertions.assertThat(foundRide.getPassengers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw exception for no active rides")
    public void shouldThrowExceptionForNoActiveRidesPassenger() {
        Passenger passengerMain = new Passenger();
        Passenger passenger = new Passenger();
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);

        Ride ride = new Ride();
        ride.setId(1);
        ride.setPassengers(passengers);

        Mockito.when(rideRepository.findActiveByPassenger(passengerMain, RideStatus.STARTED)).thenReturn(new ArrayList<>());
        assertThrows(NoActiveRideException.class, () -> {
            rideService.findActiveByPassenger(passengerMain);
        });
    }

    @Test
    @DisplayName("Should return correct passenger")
    public void shouldReturnCorrectPassengersInRidePassengerActivr() throws NoActiveRideException {
        Passenger passengerMain = new Passenger();
        Passenger passenger = new Passenger();
        passenger.setId(1);
        Passenger passenger1 = new Passenger();
        passenger.setId(2);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);
        Set<Passenger> passengers1 = new HashSet<>();
        passengers1.add(passenger1);

        Ride ride = new Ride();
        ride.setId(1);
        ride.setPassengers(passengers);

        Ride ride1 = new Ride();
        ride.setId(2);
        ride.setPassengers(passengers1);

        List<Ride> rides = new ArrayList<>();
        rides.add(ride);
        rides.add(ride1);

        Mockito.when(rideRepository.findActiveByPassenger(passengerMain, RideStatus.STARTED)).thenReturn(rides);
        Mockito.when(rideService.findPassengersForRide(2)).thenReturn(new ArrayList<>(passengers));
        Ride foundRide = rideService.findActiveByPassenger(passengerMain);
        List<Passenger> foundPassengers = new ArrayList<>(foundRide.getPassengers());
        Assertions.assertThat(foundRide.getId()).isEqualTo(2);
        Assertions.assertThat(foundPassengers.get(0).getId()).isEqualTo(2);
    }

    //Method getTimeOfNextRidesByDriverAtChosenDay
    @Test
    @DisplayName("Should return not null for valid")
    public void shouldReturnNonNullForValid() {
        Integer driverId = 1;
        LocalDate now = LocalDate.of(2022, 10, 15);
        Mockito.when(rideRepository.sumEstimatedTimeOfNextRidesByDriverAtThatDay(driverId, now)).thenReturn(10d);
        Assertions.assertThat(rideService.getTimeOfNextRidesByDriverAtChoosedDay(driverId, now)).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should return null for invalid")
    public void shouldReturnNullForInvalid() {
        Integer driverId = -1;
        LocalDate now = LocalDate.of(2022, 10, 15);
        Mockito.when(rideRepository.sumEstimatedTimeOfNextRidesByDriverAtThatDay(driverId, now)).thenReturn(null);
        Assertions.assertThat(rideService.getTimeOfNextRidesByDriverAtChoosedDay(driverId, now)).isEqualTo(0.0d);
    }

    //Method hasIntersectionBetweenRides
    @Test
    @DisplayName("Should return null for overlapping rides")
    public void shouldReturnTrueForOverlappingRides() {
        LocalDateTime rideStart = LocalDateTime.of(2022, 10, 15, 10, 0);
        LocalDateTime rideEnd = LocalDateTime.of(2022, 10, 15, 11, 0);
        LocalDateTime newRideStart = LocalDateTime.of(2022, 10, 15, 9, 30);
        LocalDateTime newRideEnd = LocalDateTime.of(2022, 10, 15, 11, 30);
        Assertions.assertThat(rideService.hasIntersectionBetweenRides(rideStart, rideEnd, newRideStart, newRideEnd)).isEqualTo(true);
    }

    @Test
    @DisplayName("Should return false for non overlapping rides")
    public void shouldReturnFalseForNonOverlappingRides() {
        LocalDateTime rideStart = LocalDateTime.of(2022, 10, 15, 10, 0);
        LocalDateTime rideEnd = LocalDateTime.of(2022, 10, 15, 11, 0);
        LocalDateTime newRideStart = LocalDateTime.of(2022, 10, 15, 11, 0);
        LocalDateTime newRideEnd = LocalDateTime.of(2022, 10, 15, 12, 0);
        Assertions.assertThat(rideService.hasIntersectionBetweenRides(rideStart, rideEnd, newRideStart, newRideEnd)).isEqualTo(false);
    }

    @Test
    @DisplayName("Should return true for ride fully contained")
    public void shouldReturnTrueForRideFullyContained() {
        LocalDateTime rideStart = LocalDateTime.of(2022, 10, 15, 10, 0);
        LocalDateTime rideEnd = LocalDateTime.of(2022, 10, 15, 11, 0);
        LocalDateTime newRideStart = LocalDateTime.of(2022, 10, 15, 10, 30);
        LocalDateTime newRideEnd = LocalDateTime.of(2022, 10, 15, 10, 45);
        Assertions.assertThat(rideService.hasIntersectionBetweenRides(rideStart, rideEnd, newRideStart, newRideEnd)).isEqualTo(true);
    }

    @Test
    @DisplayName("Should return true for ride fully containing other ride")
    public void shouldReturnTrueForRideFullyContaining() {
        LocalDateTime rideStart = LocalDateTime.of(2022, 10, 15, 10, 0);
        LocalDateTime rideEnd = LocalDateTime.of(2022, 10, 15, 11, 0);
        LocalDateTime newRideStart = LocalDateTime.of(2022, 10, 15, 9, 30);
        LocalDateTime newRideEnd = LocalDateTime.of(2022, 10, 15, 11, 30);
        Assertions.assertThat(rideService.hasIntersectionBetweenRides(rideStart, rideEnd, newRideStart, newRideEnd)).isEqualTo(true);
    }

    @Test
    @DisplayName("Should return false for ride starting and ending before")
    public void shouldReturnFalseForNewRideStartingAndEndingBefore() {
        LocalDateTime rideStart = LocalDateTime.of(2022, 10, 15, 10, 0);
        LocalDateTime rideEnd = LocalDateTime.of(2022, 10, 15, 11, 0);
        LocalDateTime newRideStart = LocalDateTime.of(2022, 10, 15, 9, 0);
        LocalDateTime newRideEnd = LocalDateTime.of(2022, 10, 15, 9, 30);
        Assertions.assertThat(rideService.hasIntersectionBetweenRides(rideStart, rideEnd, newRideStart, newRideEnd)).isEqualTo(false);
    }

    @Test
    @DisplayName("Should return false for new ride starting and ending before an existing ride")
    public void shouldReturnFalseForNewRideStartingAndEndingAfter() {
        LocalDateTime rideStart = LocalDateTime.of(2022, 10, 15, 10, 0);
        LocalDateTime rideEnd = LocalDateTime.of(2022, 10, 15, 11, 0);
        LocalDateTime newRideStart = LocalDateTime.of(2022, 10, 15, 11, 0);
        LocalDateTime newRideEnd = LocalDateTime.of(2022, 10, 15, 12, 0);
        Assertions.assertThat(rideService.hasIntersectionBetweenRides(rideStart, rideEnd, newRideStart, newRideEnd)).isEqualTo(false);
    }

    //Method findCriticalRide
    @Test
    @DisplayName("Should return null for no intersecting rides")
    public void shouldReturnNullForNoIntersectingRides() {
        List<Ride> rides = new ArrayList<>();
        Ride ride1 = new Ride();
        ride1.setStartTime(LocalDateTime.of(2022, 10, 15, 10, 0));
        ride1.setEstimatedTime(30d);
        Ride ride2 = new Ride();
        ride2.setStartTime(LocalDateTime.of(2022, 10, 15, 12, 0));
        ride2.setEstimatedTime(30d);
        rides.add(ride1);
        rides.add(ride2);

        Ride newRide = new Ride();
        newRide.setStartTime(LocalDateTime.of(2022, 10, 15, 11, 0));
        newRide.setEstimatedTime(30d);

        Assertions.assertThat(rideService.findCriticalRide(new HashSet<>(rides), newRide)).isEqualTo(null);
    }

    @Test
    @DisplayName("Should return ride with critical ride")
    public void shouldReturnRideWithCriticalRide() {
        List<Ride> rides = new ArrayList<>();
        Ride ride1 = new Ride();
        ride1.setStartTime(LocalDateTime.of(2022, 10, 15, 10, 0));
        ride1.setEstimatedTime(30d);
        Ride ride2 = new Ride();
        ride2.setStartTime(LocalDateTime.of(2022, 10, 15, 12, 0));
        ride2.setEstimatedTime(30d);
        rides.add(ride1);
        rides.add(ride2);

        Ride newRide = new Ride();
        newRide.setStartTime(LocalDateTime.of(2022, 10, 15, 12, 15));
        newRide.setEstimatedTime(30d);

        Ride foundRide = rideService.findCriticalRide(new HashSet<>(rides), newRide);
        Assertions.assertThat(foundRide.getStartTime()).isEqualTo(LocalDateTime.of(2022, 10, 15, 12, 0));
        Assertions.assertThat(foundRide.getEstimatedTime()).isEqualTo(30d);
    }

    @Test
    @DisplayName("Should return critical ride")
    public void shouldReturnCriticalRides() {
        List<Ride> rides = new ArrayList<>();
        Ride ride1 = new Ride();
        ride1.setStartTime(LocalDateTime.of(2022, 10, 15, 10, 0));
        ride1.setEstimatedTime(30d);
        Ride ride2 = new Ride();
        ride2.setStartTime(LocalDateTime.of(2022, 10, 15, 11, 0));
        ride2.setEstimatedTime(30d);
        Ride ride3 = new Ride();
        ride3.setStartTime(LocalDateTime.of(2022, 10, 15, 12, 0));
        ride3.setEstimatedTime(30d);
        rides.add(ride1);
        rides.add(ride2);
        rides.add(ride3);

        Ride newRide = new Ride();
        newRide.setStartTime(LocalDateTime.of(2022, 10, 15, 11, 0));
        newRide.setEstimatedTime(30d);

        Ride foundRide = rideService.findCriticalRide(new HashSet<>(rides), newRide);
        Assertions.assertThat(foundRide.getStartTime()).isEqualTo(LocalDateTime.of(2022, 10, 15, 11, 0));
        Assertions.assertThat(foundRide.getEstimatedTime()).isEqualTo(30d);
    }

    @Test
    @DisplayName("Should return critical ride")
    public void shouldReturnCriticalRideWithRideContained() {
        List<Ride> rides = new ArrayList<>();
        Ride ride1 = new Ride();
        ride1.setStartTime(LocalDateTime.of(2022, 10, 15, 10, 0));
        ride1.setEstimatedTime(60d);
        rides.add(ride1);

        Ride newRide = new Ride();
        newRide.setStartTime(LocalDateTime.of(2022, 10, 15, 10, 30));
        newRide.setEstimatedTime(60d);

        Ride foundRide = rideService.findCriticalRide(new HashSet<>(rides), newRide);
        Assertions.assertThat(foundRide.getStartTime()).isEqualTo(LocalDateTime.of(2022, 10, 15, 10, 0));
        Assertions.assertThat(foundRide.getEstimatedTime()).isEqualTo(60d);
    }

    @Test
    @DisplayName("Should return critical ride")
    public void shouldReturnCriticalRideWithNewRideFullyContaining() {
        List<Ride> rides = new ArrayList<>();
        Ride ride1 = new Ride();
        ride1.setStartTime(LocalDateTime.of(2022, 10, 15, 10, 0));
        ride1.setEstimatedTime(30d);
        rides.add(ride1);

        Ride newRide = new Ride();
        newRide.setStartTime(LocalDateTime.of(2022, 10, 15, 9, 30));
        newRide.setEstimatedTime(60d);

        Ride foundRide = rideService.findCriticalRide(new HashSet<>(rides), newRide);
        Assertions.assertThat(foundRide.getStartTime()).isEqualTo(LocalDateTime.of(2022, 10, 15, 10, 0));
        Assertions.assertThat(foundRide.getEstimatedTime()).isEqualTo(30d);
    }

    @Test
    @DisplayName("Should return null")
    public void shouldReturnNullRideWithNewRideStartingAndEndingBeforeExistingRide() {
        List<Ride> rides = new ArrayList<>();
        Ride ride1 = new Ride();
        ride1.setStartTime(LocalDateTime.of(2022, 10, 15, 10, 0));
        ride1.setEstimatedTime(30d);
        rides.add(ride1);

        Ride newRide = new Ride();
        newRide.setStartTime(LocalDateTime.of(2022, 10, 15, 9, 0));
        newRide.setEstimatedTime(30d);

        Ride foundRide = rideService.findCriticalRide(new HashSet<>(rides), newRide);
        Assertions.assertThat(foundRide).isEqualTo(null);
    }

    //Method cancel
    @Test
    @DisplayName("Should cancel ride")
    public void shouldCancelPendingRide() throws RideStatusException {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.PENDING);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        Mockito.when(rideRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Ride newRide = rideService.cancel(ride.getId());
        Assertions.assertThat(newRide.getId()).isEqualTo(1);
        Assertions.assertThat(newRide.getRideStatus()).isEqualTo(RideStatus.CANCELED);
    }

    @Test
    @DisplayName("Should cancel ride")
    public void shouldCancelStartedRide() throws RideStatusException {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.STARTED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        Mockito.when(rideRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Ride newRide = rideService.cancel(ride.getId());
        Assertions.assertThat(newRide.getId()).isEqualTo(1);
        Assertions.assertThat(newRide.getRideStatus()).isEqualTo(RideStatus.CANCELED);
    }

    @Test
    @DisplayName("Should not cancel ride")
    public void shouldNotCancelFinishedRide() {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.FINISHED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        assertThrows(RideStatusException.class, () -> {
            rideService.cancel(ride.getId());
        });
    }

    @Test
    @DisplayName("Should not cancel ride")
    public void shouldNotCancelNonExistingRide() {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.FINISHED);

        Mockito.when(rideRepository.findById(ride.getId())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> {
            rideService.cancel(ride.getId());
        });
    }

    //Method start
    @Test
    @DisplayName("Should start ride")
    public void shouldStartAcceptedRide() throws RideStatusException {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.ACCEPTED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        Mockito.when(rideRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Ride newRide = rideService.start(ride.getId());
        Assertions.assertThat(newRide.getId()).isEqualTo(1);
        Assertions.assertThat(newRide.getRideStatus()).isEqualTo(RideStatus.STARTED);
    }

    @Test
    @DisplayName("Should not start ride")
    public void shouldNotStartPendingRide() {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.PENDING);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        RideStatusException exception = assertThrows(RideStatusException.class, () -> {
            rideService.start(ride.getId());
        });

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot start a ride that is not in status ACCEPTED!");
    }

    @Test
    @DisplayName("Should not start ride")
    public void shouldNotStartStartedRide() {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.STARTED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        RideStatusException exception = assertThrows(RideStatusException.class, () -> {
            rideService.start(ride.getId());
        });

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot start a ride that is not in status ACCEPTED!");
    }

    @Test
    @DisplayName("Should not start ride")
    public void shouldNotStartNonExistentRide() {
        Mockito.when(rideRepository.findById(1)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> {
            rideService.start(1);
        });
    }

    //Method accept
    @Test
    @DisplayName("Should accept ride")
    public void shouldAcceptPendingRide() throws RideStatusException {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.PENDING);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        Mockito.when(rideRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Ride newRide = rideService.accept(ride.getId());
        Assertions.assertThat(newRide.getId()).isEqualTo(1);
        Assertions.assertThat(newRide.getRideStatus()).isEqualTo(RideStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Should not accept ride")
    public void shouldNotAcceptAcceptedRide() {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.ACCEPTED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        RideStatusException exception = assertThrows(RideStatusException.class, () -> {
            rideService.accept(ride.getId());
        });

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot accept a ride that is not in status PENDING!");
    }

    @Test
    @DisplayName("Should not accept ride")
    public void shouldNotAcceptStartedRide() {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.STARTED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        RideStatusException exception = assertThrows(RideStatusException.class, () -> {
            rideService.accept(ride.getId());
        });

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot accept a ride that is not in status PENDING!");
    }

    @Test
    @DisplayName("Should not accept ride")
    public void shouldNotAcceptCanceledRide() {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.CANCELED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        RideStatusException exception = assertThrows(RideStatusException.class, () -> {
            rideService.accept(ride.getId());
        });

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot accept a ride that is not in status PENDING!");
    }

    @Test
    @DisplayName("Should not accept ride")
    public void shouldNotAcceptNonExistentRide() {
        Mockito.when(rideRepository.findById(1)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> {
            rideService.accept(1);
        });
    }

    //Method end
    @Test
    @DisplayName("Should end ride")
    public void shouldEndStartedRide() throws RideStatusException {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.STARTED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        Mockito.when(rideRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Ride newRide = rideService.end(ride.getId());
        Assertions.assertThat(newRide.getId()).isEqualTo(1);
        Assertions.assertThat(newRide.getRideStatus()).isEqualTo(RideStatus.FINISHED);
    }

    @Test
    @DisplayName("Should not end ride")
    public void shouldNotEndPendingRide() {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.PENDING);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        RideStatusException exception = assertThrows(RideStatusException.class, () -> {
            rideService.end(ride.getId());
        });

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot end a ride that is not in status ACTIVE!");
    }

    @Test
    @DisplayName("Should not end ride")
    public void shouldNotEndNonExistingRide() {
        Mockito.when(rideRepository.findById(1)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> {
            rideService.start(1);
        });
    }

    //Method reject
    @Test
    @DisplayName("Should reject ride")
    public void shouldRejectPendingRide() throws RideStatusException {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.PENDING);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        Mockito.when(rideRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Ride newRide = rideService.reject(ride.getId(), "Reason");
        Assertions.assertThat(newRide.getId()).isEqualTo(1);
        Assertions.assertThat(newRide.getRideStatus()).isEqualTo(RideStatus.REJECTED);
        Assertions.assertThat(newRide.getRideRejection().getReason()).isEqualTo("Reason");
    }

    @Test
    @DisplayName("Should reject ride")
    public void shouldRejectAcceptedRide() throws RideStatusException {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.ACCEPTED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        Mockito.when(rideRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Ride newRide = rideService.reject(ride.getId(), "Reason");
        Assertions.assertThat(newRide.getId()).isEqualTo(1);
        Assertions.assertThat(newRide.getRideStatus()).isEqualTo(RideStatus.REJECTED);
        Assertions.assertThat(newRide.getRideRejection().getReason()).isEqualTo("Reason");
    }

    @Test
    @DisplayName("Should not reject ride")
    public void shouldNotRejectStartedRide() {
        Ride ride = new Ride();
        ride.setId(1);
        ride.setRideStatus(RideStatus.STARTED);

        Mockito.when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        RideStatusException exception = assertThrows(RideStatusException.class, () -> {
            rideService.reject(ride.getId(), "Reason");
        });

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot cancel a ride that is not in status PENDING or ACCEPTED!");
    }

    @Test
    @DisplayName("Should not reject ride")
    public void shouldNotRejectNonExistentRide() {
        Mockito.when(rideRepository.findById(1)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> {
            rideService.reject(1, "Reason");
        });
    }
}

