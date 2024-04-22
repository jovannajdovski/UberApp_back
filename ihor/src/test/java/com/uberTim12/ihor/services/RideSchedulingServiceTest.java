package com.uberTim12.ihor.services;

import com.uberTim12.ihor.exception.CannotScheduleDriveException;
import com.uberTim12.ihor.model.ride.ActiveDriver;
import com.uberTim12.ihor.model.ride.ActiveDriverCriticalRide;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import com.uberTim12.ihor.repository.ride.IActiveDriverRepository;
import com.uberTim12.ihor.service.ride.impl.RideSchedulingService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import net.minidev.json.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RideSchedulingServiceTest {
    @InjectMocks
    private RideSchedulingService rideSchedulingService;
    @Mock
    private IVehicleService vehicleService;
    @Mock
    private IWorkHoursService workHoursService;
    @Mock
    private IActiveDriverRepository activeDriverRepository;
    @Mock
    private IRideService rideService;
    @Mock
    private ILocationService locationService;
    @Mock
    private IPassengerService passengerService;
    @Mock
    private IDriverService driverService;

    public RideSchedulingServiceTest(){

    }
    // Estimated time
    @Test
    @DisplayName("Should throw null pointer exception caused by accessing paths of ride which is null")
    public void shouldThrowNullPointerExceptionForAccessingNullablePaths() throws NullPointerException{
        Ride ride = null;
        assertThrows(NullPointerException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }
    @Test
    @DisplayName("Should throw null pointer exception caused by accessing start and end points of ride path which is null")
    public void shouldThrowNullPointerExceptionForAccessingNullablePathPoints() throws NullPointerException{
        Ride ride = new Ride();
        HashSet<Path> paths=null;
        ride.setPaths(paths);
        assertThrows(NullPointerException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }
    // busy passenger
    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by passengers are not free for the ride with valid ride path")
    public void shouldThrowCannotScheduleDriveExceptionForBusyPassengersWithValidRidePath() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());
        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(false);

        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }
    //getQualifiedDrivers()=null
    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by no active drivers")
    public void shouldThrowCannotScheduleDriveExceptionForNoActiveDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());
        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(driverService.sortPerEndOfCriticalRide(Collections.emptyList(),ride)).thenReturn(Collections.emptyList());
        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }
    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by no active drivers with appropriate vehicle")
    public void shouldThrowCannotScheduleDriveExceptionForNoActiveDriverWithAppropriateVehicle() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());
        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(false);

        Mockito.when(driverService.sortPerEndOfCriticalRide(Collections.emptyList(),ride)).thenReturn(Collections.emptyList());
        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }
    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by no active drivers with free work hours")
    public void shouldThrowCannotScheduleDriveExceptionForNoActiveDriverWithFreeWorkHours() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());
        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false);

        Mockito.when(driverService.sortPerEndOfCriticalRide(Collections.emptyList(),ride)).thenReturn(Collections.emptyList());
        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }

    //not available right now and for the next half hour
    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by active drivers are not free at the moment with error in critical ride")
    public void shouldThrowCannotScheduleDriveExceptionForBusyActiveDriversAtTheMomentWithErrorInCriticalRide() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());
        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false);
        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(Collections.emptyList());
        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }
    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by active drivers are not free for the next half an hour")
    public void shouldThrowCannotScheduleDriveExceptionForBusyActiveDriversForNextHalfHour() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());
        
        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride));
        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false);
        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);
        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }

    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by active drivers are not free at the moment with insufficient number of seats")
    public void shouldThrowCannotScheduleDriveExceptionForBusyActiveDriversAtTheMomentWithInsufficientNumberOfSeats() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(LocalDateTime.now());
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride1));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(0);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(0);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false).thenReturn(true);
//        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);
        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }

    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by active drivers have insufficient number of seats with error in critical ride")
    public void shouldThrowCannotScheduleDriveExceptionForActiveDriversHaveInsufficientNumberOfSeatsWithErrorInCriticalRide() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(0);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(0);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);
        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(Collections.emptyList());

        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }

    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by active drivers have insufficient number of seats and are not free for the next half an hour")
    public void shouldThrowCannotScheduleDriveExceptionForActiveDriversHaveInsufficientNumberOfSeatsAndBusyForNextHalfHour() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(LocalDateTime.now());
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride1));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(0);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(0);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);


        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(false);
//        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false);
        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);
        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }

    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by active drivers has insufficient number of seats")
    public void shouldThrowCannotScheduleDriveExceptionForFreeActiveDriverWithInsufficientNumberOfSeats() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(LocalDateTime.now());
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride1));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(0);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(0);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.any(Ride.class))).thenReturn(true);
        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);
        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }

    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by active drivers are too far with error in critical ride")
    public void shouldThrowCannotScheduleDriveExceptionForTooFarActiveDriversWithErrorInCriticalRide() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);
        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(Collections.emptyList());

        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }

    @Test
    @DisplayName("Should throw CannotScheduleDriveException caused by active drivers are to far and not free for the next half an hour")
    public void shouldThrowCannotScheduleDriveExceptionForTooFarActiveDriversAndBusyForNextHalfHour() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        ride.setStartTime(LocalDateTime.now());

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(LocalDateTime.now());
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride1));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(false);
        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);
        assertThrows(CannotScheduleDriveException.class, () -> {
            rideSchedulingService.findFreeVehicle(ride);
        });
    }

    //happy path findClosest successful
    @Test
    @DisplayName("Should return ride with normal value for estimated time, normal value for total price and second qualified driver because first is too far")
    public void shouldReturnRideWithNormalEstimatedTimeAndPriceAndSecondQualifiedDriverCausedByMaxDistanceOfFirst() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class).thenReturn(2d).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
        
    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, normal value for total price and second qualified driver because first is too far")
    public void shouldReturnRideWithMaxEstimatedTimeAndNormalPriceAndSecondQualifiedDriverCausedByMaxDistanceOfFirst() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class).thenReturn(2d).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
        
    }
    @Test
    @DisplayName("Should return ride with normal value for estimated time, DOUBLE_MAX value for total price and second qualified driver because first is too far")
    public void shouldReturnRideWithNormalEstimatedTimeAndMaxPriceAndSecondQualifiedDriverCausedByMaxDistanceOfFirst() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class).thenReturn(2d).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
        
    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, DOUBLE_MAX value for total price and second qualified driver because first is too far")
    public void shouldReturnRideWitMaxEstimatedTimeAndMaxPriceAndSecondQualifiedDriverCausedByMaxDistanceOfFirst() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class).thenReturn(2d).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
        
    }

    @Test
    @DisplayName("Should return ride with normal value for estimated time, normal value for total price and first qualified driver because second is too far")
    public void shouldReturnRideWithNormalEstimatedTimeAndPriceAndFirstQualifiedDriverCausedByMaxDistanceOfSecond() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(2d).thenThrow(ParseException.class).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, normal value for total price and first qualified driver because second is too far")
    public void shouldReturnRideWithMaxEstimatedTimeAndNormalPriceAndFirstQualifiedDriverCausedByMaxDistanceOfSecond() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(2d).thenThrow(ParseException.class).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
    }
    @Test
    @DisplayName("Should return ride with normal value for estimated time, DOUBLE_MAX value for total price and first qualified driver because second is too far")
    public void shouldReturnRideWithNormalEstimatedTimeAndMaxPriceAndFirstQualifiedDriverCausedByMaxDistanceOfSecond() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(2d).thenThrow(ParseException.class).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, DOUBLE_MAX value for total price and first qualified driver second first is too far")
    public void shouldReturnRideWitMaxEstimatedTimeAndMaxPriceAndFirstQualifiedDriverCausedByMaxDistanceOfSecond() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(2d).thenThrow(ParseException.class).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
    }

    @Test
    @DisplayName("Should return ride with normal value for estimated time, normal value for total price and second qualified driver because first is further away")
    public void shouldReturnRideWithNormalEstimatedTimeAndPriceAndSecondQualifiedDriverCausedByGreaterDistanceOfFirst() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(2.00001).thenReturn(2d).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, normal value for total price and second qualified driver because first is further away")
    public void shouldReturnRideWithMaxEstimatedTimeAndNormalPriceAndSecondQualifiedDriverCausedByGreaterDistanceOfFirst() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(2.00001).thenReturn(2d).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
    }
    @Test
    @DisplayName("Should return ride with normal value for estimated time, DOUBLE_MAX value for total price and second qualified driver because first is further away")
    public void shouldReturnRideWithNormalEstimatedTimeAndMaxPriceAndSecondQualifiedDriverCausedByGreaterDistanceOfFirst() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(2.00001).thenReturn(2d).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, DOUBLE_MAX value for total price and second qualified driver because first is further away")
    public void shouldReturnRideWithMaxEstimatedTimeAndMaxPriceAndSecondQualifiedDriverCausedByGreaterDistanceOfFirst() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(2.1).thenReturn(2d).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime);
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());
    }

    // happy path find in next half an hour
    @Test
    @DisplayName("Should return ride with normal value for estimated time, normal value for total price, scheduled in future with second driver due to max distance of all drivers and busyness of first chosen driver")
    public void shouldReturnRideWithNormalEstimatedTimeAndPriceInFutureWithSecondQualifiedDriverDueToMaxDistanceOfAllDriversAndBusynessOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class).thenThrow(ParseException.class).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(20));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, normal value for total price, scheduled in future with second driver due to max distance of all drivers and busyness of first chosen driver")
    public void shouldReturnRideWithMaxEstimatedTimeAndPriceInFutureWithSecondQualifiedDriverDueToToMaxDistanceOfAllDriversAndBusynessOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);;

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class).thenThrow(ParseException.class).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(20));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with normal value for estimated time, DOUBLE_MAX value for total price, scheduled in future with second driver due to max distance of all drivers and busyness of first chosen driver")
    public void shouldReturnRideWithNormalEstimatedTimeAndMaxPriceInFutureWithSecondQualifiedDriverDueToMaxDistanceOfAllDriversAndBusynessOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class).thenThrow(ParseException.class).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(20));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, DOUBLE_MAX value for total price, scheduled in future with second driver due to max distance of all drivers and busyness of first chosen driver")
    public void shouldReturnRideWithMaxEstimatedTimeAndMaxPriceInFutureWithSecondQualifiedDriverDueToMaxDistanceOfAllDriversAndBusynessOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class).thenThrow(ParseException.class).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(20));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }

    @Test
    @DisplayName("Should return ride with normal value for estimated time, normal value for total price, scheduled in future with second driver due to busyness of all drivers and busyness of first chosen driver")
    public void shouldReturnRideWithNormalEstimatedTimeAndPriceInFutureWithSecondQualifiedDriverDueToBusynessOfAllDriversAndBusynessOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(20));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, normal value for total price, scheduled in future with second driver due to busyness of all drivers and busyness of first chosen driver")
    public void shouldReturnRideWithMaxEstimatedTimeAndPriceInFutureWithSecondQualifiedDriverDueToBusynessOfAllDriversAndBusynessOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);;

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(20));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with normal value for estimated time, DOUBLE_MAX value for total price, scheduled in future with second driver due to busyness of all drivers and busyness of first chosen driver")
    public void shouldReturnRideWithNormalEstimatedTimeAndMaxPriceInFutureWithSecondQualifiedDriverDueToBusynessOfAllDriversAndBusynessOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(20));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, DOUBLE_MAX value for total price, scheduled in future with second driver due to busyness of all drivers and busyness of first chosen driver")
    public void shouldReturnRideWithMaxEstimatedTimeAndMaxPriceInFutureWithSecondQualifiedDriverDueToBusynessOfAllDriversAndBusynessOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+500));
        assertEquals(ride.getDriver(),activeDrivers.get(1).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(20));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(1).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }

    @Test
    @DisplayName("Should return ride with normal value for estimated time, normal value for total price, scheduled in future with first driver due to max distance of all drivers and availability of first chosen driver")
    public void shouldReturnRideWithNormalEstimatedTimeAndPriceInFutureWithFirstQualifiedDriverDueToMaxDistanceOfAllDriversAndAvailabilityOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class).thenThrow(ParseException.class).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(true).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(10));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, normal value for total price, scheduled in future with first driver due to max distance of all drivers and availability of first chosen driver")
    public void shouldReturnRideWithMaxEstimatedTimeAndPriceInFutureWithFirstQualifiedDriverDueToToMaxDistanceOfAllDriversAndAvailabilityOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);;

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class).thenThrow(ParseException.class).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(true).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(10));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with normal value for estimated time, DOUBLE_MAX value for total price, scheduled in future with first driver due to max distance of all drivers and availability of first chosen driver")
    public void shouldReturnRideWithNormalEstimatedTimeAndMaxPriceInFutureWithFirstQualifiedDriverDueToMaxDistanceOfAllDriversAndAvailabilityOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class).thenThrow(ParseException.class).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(true).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(10));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, DOUBLE_MAX value for total price, scheduled in future with first driver due to max distance of all drivers and availability of first chosen driver")
    public void shouldReturnRideWithMaxEstimatedTimeAndMaxPriceInFutureWithFirstQualifiedDriverDueToMaxDistanceOfAllDriversAndAvailabilityOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class).thenThrow(ParseException.class).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true).thenReturn(true).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(10));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }

    @Test
    @DisplayName("Should return ride with normal value for estimated time, normal value for total price, scheduled in future with first driver due to busyness of all drivers and availability of first chosen driver")
    public void shouldReturnRideWithNormalEstimatedTimeAndPriceInFutureWithFirstQualifiedDriverDueToBusynessOfAllDriversAndAvailabilityOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(10));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, normal value for total price, scheduled in future with first driver due to busyness of all drivers and availability of first chosen driver")
    public void shouldReturnRideWithMaxEstimatedTimeAndPriceInFutureWithFirstQualifiedDriverDueToBusynessOfAllDriversAndAvailabilityOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);;

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*10d+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(10));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with normal value for estimated time, DOUBLE_MAX value for total price, scheduled in future with first driver due to busyness of all drivers and availability of first chosen driver")
    public void shouldReturnRideWithNormalEstimatedTimeAndMaxPriceInFutureWithFirstQualifiedDriverDueToBusynessOfAllDriversAndAvailabilityOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenReturn(10d);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),10d);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(10));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }
    @Test
    @DisplayName("Should return ride with DOUBLE_MAX value for estimated time, DOUBLE_MAX value for total price, scheduled in future with first driver due to busyness of all drivers and availability of first chosen driver")
    public void shouldReturnRideWithMaxEstimatedTimeAndMaxPriceInFutureWithFirstQualifiedDriverDueToBusynessOfAllDriversAndAvailabilityOfFirstChosenDriver() throws CannotScheduleDriveException, IOException, ParseException {
        Ride ride= getRideWithValidPathsAndOnePassenger();
        LocalDateTime startTime=LocalDateTime.now();
        ride.setStartTime(startTime);

        Mockito.when(locationService.calculateEstimatedTime(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(IOException.class);

        Mockito.when(passengerService.isPassengersFree(ride)).thenReturn(true);

        List<ActiveDriver> activeDrivers=getActiveDrivers();
        List<ActiveDriverCriticalRide> activeDriverCriticalRides=new ArrayList<ActiveDriverCriticalRide>();
        Ride ride1=new Ride();
        ride1.setEstimatedTime(10d);
        ride1.setStartTime(startTime);
        Ride ride2=new Ride();
        ride2.setEstimatedTime(20d);
        ride2.setStartTime(startTime);
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(0),ride1));
        activeDriverCriticalRides.add(new ActiveDriverCriticalRide(activeDrivers.get(1),ride2));
        activeDrivers.get(0).getDriver().getVehicle().setSeats(5);
        activeDrivers.get(1).getDriver().getVehicle().setSeats(5);

        Mockito.when(activeDriverRepository.findAll()).thenReturn(activeDrivers);

        Mockito.when(locationService.calculateDistance(Mockito.any(Location.class),Mockito.any(Location.class))).thenThrow(ParseException.class);

        Mockito.when(vehicleService.isVehicleMeetCriteria(Mockito.any(Vehicle.class), Mockito.eq(ride))).thenReturn(true);
        Mockito.when(workHoursService.isDriverAvailable(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(true);

        Mockito.when(driverService.sortPerEndOfCriticalRide(activeDrivers,ride)).thenReturn(activeDriverCriticalRides);

        Mockito.when(driverService.isDriverFreeForRide(Mockito.any(Driver.class),Mockito.eq(ride))).thenReturn(false).thenReturn(false).thenReturn(true);

        Mockito.when(rideService.save(ride)).thenReturn(ride);

        ride=rideSchedulingService.findFreeVehicle(ride);
        assertEquals(ride.getEstimatedTime(),Double.MAX_VALUE);
        assertEquals(ride.getRideStatus(), RideStatus.PENDING);
        assertEquals(ride.getTotalPrice(),(double)Math.round(120*Double.MAX_VALUE+300));
        assertEquals(ride.getDriver(),activeDrivers.get(0).getDriver());
        assertEquals(ride.getStartTime(),startTime.plusMinutes(10));
        assertEquals(ride.getPassengers().size(),1);
        assertEquals(ride.getVehicleType(),activeDrivers.get(0).getDriver().getVehicle().getVehicleType());
        assertNull(ride.getEndTime());

    }

    private Ride getRideWithValidPathsAndOnePassenger(){
        Ride ride = new Ride();
        Path path = new Path();
        Location startPoint = new Location("Bulevar Cara Lazara 90", 45.2405129,19.8265563);
        Location endPoint = new Location("Bulevar Patrijaha Pavla 2", 45.239840,19.820620 );
        path.setStartPoint(startPoint);
        path.setEndPoint(endPoint);
        Set<Path> paths = new HashSet<>();
        paths.add(path);
        ride.setPaths(paths);
        HashSet<Passenger> passengers=new HashSet<>();
        passengers.add(new Passenger());
        ride.setPassengers(passengers);
        return ride;
    }
    private List<ActiveDriver> getActiveDrivers(){
        ActiveDriver activeDriver1=new ActiveDriver();
        Driver driver1=new Driver();
        Vehicle vehicle1=new Vehicle();
        vehicle1.setVehicleType(new VehicleType(VehicleCategory.STANDARD,300d));
        driver1.setVehicle(vehicle1);
        activeDriver1.setDriver(driver1);
        activeDriver1.setLocation(new Location("Bulevar Cara Lazara 90", 45.2405129,19.8265563));

        ActiveDriver activeDriver2=new ActiveDriver();
        Driver driver2=new Driver();
        Vehicle vehicle2=new Vehicle();
        vehicle2.setVehicleType(new VehicleType(VehicleCategory.LUXURY,500d));
        driver2.setVehicle(vehicle2);
        activeDriver2.setDriver(driver2);
        activeDriver2.setLocation(new Location("Bulevar Cara Lazara 90", 45.2405129,19.8265563));


        List<ActiveDriver> activeDrivers=new ArrayList<>();
        activeDrivers.add(activeDriver1);
        activeDrivers.add(activeDriver2);
        return activeDrivers;
    }
}
