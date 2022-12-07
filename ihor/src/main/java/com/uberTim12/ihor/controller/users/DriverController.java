package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.users.DriverDTO;
import com.uberTim12.ihor.dto.users.DriverDocumentDTO;
import com.uberTim12.ihor.dto.users.WorkHoursDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.dto.ride.RideDTO;
import com.uberTim12.ihor.model.users.*;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.dto.vehicle.VehicleDTO;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.impl.DriverDocumentService;
import com.uberTim12.ihor.service.users.impl.DriverService;
import com.uberTim12.ihor.service.vehicle.impl.VehicleService;
import com.uberTim12.ihor.service.users.impl.WorkHoursService;
import com.uberTim12.ihor.service.users.interfaces.IDriverDocumentService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "api/driver")
public class DriverController {

    private final IDriverService driverService;
    private final IDriverDocumentService driverDocumentService;
    private final IVehicleService vehicleService;
    private final IWorkHoursService workHoursService;
    private final IRideService rideService;

    @Autowired
    DriverController(DriverService driverService,
                     DriverDocumentService driverDocumentService,
                     VehicleService vehicleService,
                     WorkHoursService workHoursService,
                     RideService rideService) {
        this.driverService = driverService;
        this.driverDocumentService = driverDocumentService;
        this.vehicleService = vehicleService;
        this.workHoursService = workHoursService;
        this.rideService = rideService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO driverDTO) {

        Driver driver = new Driver();
        driver.setName(driverDTO.getName());
        driver.setSurname(driverDTO.getSurname());
        driver.setProfilePicture(driverDTO.getProfilePicture());
        driver.setTelephoneNumber(driverDTO.getTelephoneNumber());
        driver.setEmail(driverDTO.getEmail());
        driver.setAddress(driverDTO.getAddress());
        driver.setDocuments(new HashSet<>());
        driver.setRides(new HashSet<>());
        driver.setVehicle(new Vehicle());

        driver = driverService.save(driver);

        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DriverDTO>> getDriversPage(Pageable page) {

        Page<Driver> drivers = driverService.findAll(page);

        List<DriverDTO> driverDTOs = new ArrayList<>();
        for (Driver d : drivers) {
            driverDTOs.add(new DriverDTO(d));
        }

        return new ResponseEntity<>(driverDTOs, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable Integer id) {

        Driver driver = driverService.findOne(id);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDTO> updateDriver(@RequestBody DriverDTO driverDTO) {

        Driver driver = driverService.findOne(driverDTO.getId());

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        driver.setName(driverDTO.getName());
        driver.setSurname(driverDTO.getSurname());
        driver.setProfilePicture(driverDTO.getProfilePicture());
        driver.setTelephoneNumber(driverDTO.getTelephoneNumber());
        driver.setEmail(driverDTO.getEmail());
        driver.setAddress(driverDTO.getAddress());

        driver = driverService.save(driver);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/documents")
    public ResponseEntity<List<DriverDocumentDTO>> getDriverDocuments(@PathVariable Integer driverId) {

        Driver driver = driverService.findOneWithDocuments(driverId);

        Set<DriverDocument> documents = driver.getDocuments();
        List<DriverDocumentDTO> documentsDTO = new ArrayList<>();

        for (DriverDocument d: documents)
            documentsDTO.add(new DriverDocumentDTO(d));

        return new ResponseEntity<>(documentsDTO, HttpStatus.OK);
    }


    //IZMENI da se brise dokument
    @DeleteMapping(value = "/{documentId}/documents")
    public ResponseEntity<Void> deleteDriverDocuments(@PathVariable Integer documentId) {

        Driver driver = driverService.findOne(documentId);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        for (DriverDocument d: driver.getDocuments())
            driverDocumentService.remove(d.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}/documents", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDocumentDTO> addDocumentToDriver(@PathVariable Integer driverId,
                                                                 @RequestBody DriverDocumentDTO driverDocumentDTO) {

        Driver driver = driverService.findOne(driverId);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        DriverDocument driverDocument = new DriverDocument();
        driverDocument.setName(driverDocumentDTO.getName());
        driverDocument.setPicture(driverDocumentDTO.getPicture());
        driverDocument.setDriver(driver);

        driverDocument = driverDocumentService.save(driverDocument);

        return new ResponseEntity<>(new DriverDocumentDTO(driverDocument), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{driverId}/vehicle")
    public ResponseEntity<VehicleDTO> getDriverVehicle(@PathVariable Integer driverId) {

        Driver driver = driverService.findOneWithDocuments(driverId);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new VehicleDTO(driver.getVehicle()), HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDTO> addVehicleToDriver(@PathVariable Integer driverId,
                                                         @RequestBody VehicleDTO vehicleDTO) {

        Driver driver = driverService.findOne(driverId);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(vehicleDTO.getDriver());
        vehicle.setVehicleModel(vehicleDTO.getVehicleModel());
        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setRegistrationPlate(vehicleDTO.getRegistrationPlate());
        vehicle.setSeats(vehicleDTO.getSeats());
        vehicle.setCurrentLocation(vehicleDTO.getCurrentLocation());
        vehicle.setBabiesAllowed(vehicleDTO.isBabiesAllowed());
        vehicle.setPetsAllowed(vehicleDTO.isPetsAllowed());
        vehicle.setReviews(new HashSet<>());

        vehicle = vehicleService.save(vehicle);

        driver.setVehicle(vehicle);
        driverService.save(driver);

        return new ResponseEntity<>(new VehicleDTO(vehicle), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{driverId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDTO> changeDriverVehicle(@PathVariable Integer driverId,
                                                         @RequestBody VehicleDTO vehicleDTO) {

        Driver driver = driverService.findOne(driverId);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(driver);
        vehicle.setVehicleModel(vehicleDTO.getVehicleModel());
        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setRegistrationPlate(vehicleDTO.getRegistrationPlate());
        vehicle.setSeats(vehicleDTO.getSeats());
        vehicle.setCurrentLocation(vehicleDTO.getCurrentLocation());
        vehicle.setBabiesAllowed(vehicleDTO.isBabiesAllowed());
        vehicle.setPetsAllowed(vehicleDTO.isPetsAllowed());
        vehicle.setReviews(new HashSet<>());

        vehicle = vehicleService.save(vehicle);

        driver.setVehicle(vehicle);

        driver = driverService.save(driver);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/working-hours")
    public ResponseEntity<List<WorkHoursDTO>> getDriverWorkingHours(@PathVariable Integer driverId,
                                                                    @RequestParam int page,
                                                                    @RequestParam int size,
                                                                    @RequestParam(required = false)
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                                    @RequestParam(required = false)
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Pageable paging = PageRequest.of(page, size);

        Page<WorkHours> workHours;
        if (from == null || to == null)
            workHours = workHoursService.findFilteredWorkHours(driverId, paging);
        else
            workHours = workHoursService.findFilteredWorkHours(driverId, from, to, paging);

        List<WorkHoursDTO> workHoursDTO = new ArrayList<>();
        for (WorkHours w : workHours)
            workHoursDTO.add(new WorkHoursDTO(w));

        return new ResponseEntity<>(workHoursDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}/working-hours", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkHoursDTO> addWorkingHours(@PathVariable Integer driverId,
                                                         @RequestBody WorkHoursDTO workHoursDTO) {

        Driver driver = driverService.findOne(driverId);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        WorkHours workHours = new WorkHours();
        workHours.setDriver(driver);
        workHours.setStartTime(workHoursDTO.getStartTime());
        workHours.setEndTime(workHoursDTO.getEndTime());

        workHours = workHoursService.save(workHours);

        return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{driverId}/ride")
    public ResponseEntity<?> getRidesForDriver(@PathVariable Integer driverId,
                                                           @RequestParam int page,
                                                           @RequestParam int size,
                                                           @RequestParam(required = false)
                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime from,
                                                           @RequestParam(required = false)
                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime to) {

        Pageable paging = PageRequest.of(page, size);

        Page<Ride> rides;
        if (from == null || to == null)
            rides = rideService.findFilteredRides(driverId, paging);
        else
            rides = rideService.findFilteredRides(driverId, from, to, paging);

        List<RideDTO> rideDTOs = new ArrayList<>();
        for (Ride r : rides)
            rideDTOs.add(new RideDTO(r));

        ObjectListResponseDTO<RideDTO> res = new ObjectListResponseDTO<>(rideDTOs.size(),rideDTOs);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/working-hour/{workingHourId}")
    public ResponseEntity<WorkHoursDTO> getWorkingHours(@PathVariable Integer workingHourId) {

        WorkHours workHours = workHoursService.findOne(workingHourId);

        if (workHours == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
    }

    @PutMapping(value = "/working-hour/{workingHourId}", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkHoursDTO> changeDriverWorkingHours(@PathVariable Integer workingHourId,
                                                         @RequestBody WorkHoursDTO workHoursDTO) {

        WorkHours workHours = workHoursService.findOne(workingHourId);

        if (workHours == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        workHours.setStartTime(workHoursDTO.getStartTime());
        workHours.setEndTime(workHoursDTO.getEndTime());

        workHours = workHoursService.save(workHours);

        return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
    }
}
