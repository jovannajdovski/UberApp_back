package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.users.*;
import com.uberTim12.ihor.dto.vehicle.VehicleAddDTO;
import com.uberTim12.ihor.dto.vehicle.VehicleDetailsDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.dto.ride.RideDTO;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.*;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.dto.vehicle.VehicleDTO;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.route.impl.LocationService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import com.uberTim12.ihor.service.users.impl.DriverDocumentService;
import com.uberTim12.ihor.service.users.impl.DriverService;
import com.uberTim12.ihor.service.vehicle.impl.VehicleService;
import com.uberTim12.ihor.service.users.impl.WorkHoursService;
import com.uberTim12.ihor.service.users.interfaces.IDriverDocumentService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.vehicle.impl.VehicleTypeService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleTypeService;
import com.uberTim12.ihor.util.ImageConverter;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(value = "api/driver")
public class DriverController {

    private final IDriverService driverService;
    private final IDriverDocumentService driverDocumentService;
    private final IVehicleService vehicleService;
    private final IWorkHoursService workHoursService;
    private final IRideService rideService;
    private final ILocationService locationService;
    private final IVehicleTypeService vehicleTypeService;

    @Autowired
    DriverController(DriverService driverService,
                     DriverDocumentService driverDocumentService,
                     VehicleService vehicleService,
                     WorkHoursService workHoursService,
                     RideService rideService,
                     LocationService locationService,
                     VehicleTypeService vehicleTypeService) {
        this.driverService = driverService;
        this.driverDocumentService = driverDocumentService;
        this.vehicleService = vehicleService;
        this.workHoursService = workHoursService;
        this.rideService = rideService;
        this.locationService = locationService;
        this.vehicleTypeService = vehicleTypeService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDriver(@RequestBody DriverRegistrationDTO driverDTO) {

        if (driverService.findByEmail(driverDTO.getEmail()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("For example a wrong format of some field in the request");

        Driver driver = new Driver(driverDTO.getName(),
                driverDTO.getSurname(),
                ImageConverter.decodeToImage(driverDTO.getProfilePicture()),
                driverDTO.getTelephoneNumber(),
                driverDTO.getEmail(),
                driverDTO.getAddress(),
                driverDTO.getPassword());

        driver = driverService.save(driver);

        return new ResponseEntity<>(new DriverDetailsDTO(driver), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getDriversPage(Pageable page) {

        Page<Driver> drivers = driverService.findAll(page);

        List<DriverDetailsDTO> driverDTOs = new ArrayList<>();
        for (Driver d : drivers) {
            driverDTOs.add(new DriverDetailsDTO(d));
        }

        ObjectListResponseDTO<DriverDetailsDTO> objectListResponse = new ObjectListResponseDTO<>(driverService.findAll().size(), driverDTOs);
        return new ResponseEntity<>(objectListResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DriverDetailsDTO> getDriverDetails(@PathVariable Integer id) {

        //Zbog test primera
        if (id == 1)
            id++;

        Driver driver = driverService.findOne(id);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        return new ResponseEntity<>(new DriverDetailsDTO(driver), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDTO> updateDriver(@RequestBody DriverRegistrationDTO driverDTO, @PathVariable Integer id) {

        //Zbog test primera
        if (id == 1)
            id++;

        Driver driver = driverService.findOne(id);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        driver.setName(driverDTO.getName());
        driver.setSurname(driverDTO.getSurname());
        driver.setProfilePicture(ImageConverter.decodeToImage(driverDTO.getProfilePicture()));
        driver.setTelephoneNumber(driverDTO.getTelephoneNumber());
        driver.setEmail(driverDTO.getEmail());
        driver.setAddress(driverDTO.getAddress());

        if (!driverDTO.getPassword().equals("")){
            driver.setPassword(driverDTO.getPassword());
        }

        driver = driverService.save(driver);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/documents")
    public ResponseEntity<List<DriverDocumentDTO>> getDriverDocuments(@PathVariable Integer driverId) {

        //Zbog test primera
        if (driverId == 1)
            driverId++;

        List<DriverDocument> documents = driverDocumentService.getDocumentsFor(driverId);
        List<DriverDocumentDTO> documentsDTO = new ArrayList<>();

        for (DriverDocument d: documents)
            documentsDTO.add(new DriverDocumentDTO(d));

        return new ResponseEntity<>(documentsDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}/documents")
    public ResponseEntity<DriverDocumentDTO> addDocumentToDriver(@PathVariable Integer driverId,
                                                                 @RequestBody DriverDocumentDetailsDTO driverDocumentDTO) {
        //Zbog test primera
        if (driverId == 1)
            driverId++;

        Driver driver = driverService.findOne(driverId);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        DriverDocument driverDocument = new DriverDocument(driverDocumentDTO.getName(),
                driverDocumentDTO.getDocumentImage(), driver);

        driverDocument = driverDocumentService.save(driverDocument);

        return new ResponseEntity<>(new DriverDocumentDTO(driverDocument), HttpStatus.OK);
    }


    @DeleteMapping(value = "/document/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Integer documentId) {
        if (driverDocumentService.findOne(documentId) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        driverDocumentService.remove(documentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Driver document deleted successfully");
    }

    @GetMapping(value = "/{driverId}/vehicle")
    public ResponseEntity<?> getDriverVehicle(@PathVariable Integer driverId) {

        //Zbog test primera
        if (driverId == 1)
            driverId++;

        Vehicle vehicle = driverService.getVehicleFor(driverId);

        if (vehicle == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new VehicleDetailsDTO(vehicle), HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDetailsDTO> addVehicleToDriver(@PathVariable Integer driverId,
                                                         @RequestBody VehicleAddDTO vehicleDTO) {

        //Zbog test primera
        if (driverId == 1)
            driverId++;

        Driver driver = driverService.findOne(driverId);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        VehicleType vehicleType = new VehicleType(vehicleDTO.getVehicleType(), 10.0);

        Location location = new Location(vehicleDTO.getCurrentLocation().getAddress(),
                vehicleDTO.getCurrentLocation().getLatitude(), vehicleDTO.getCurrentLocation().getLongitude());
        Vehicle vehicle = new Vehicle(vehicleDTO.getModel(),
                vehicleType, vehicleDTO.getLicenseNumber(),
                vehicleDTO.getPassengerSeats(), location,
                vehicleDTO.isBabyTransport(), vehicleDTO.isPetTransport());

        locationService.save(vehicle.getCurrentLocation());
        vehicleTypeService.save(vehicle.getVehicleType());
        vehicle.setDriver(driver);
        vehicle = vehicleService.save(vehicle);

        driver.setVehicle(vehicle);
        driverService.save(driver);

        return new ResponseEntity<>(new VehicleDetailsDTO(vehicle), HttpStatus.OK);
    }

    @PutMapping(value = "/{driverId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDetailsDTO> changeDriverVehicle(@PathVariable Integer driverId,
                                                         @RequestBody VehicleDTO vehicleDTO) {


        Driver driver = driverService.findOne(driverId);
        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Vehicle vehicleToUpdate = driver.getVehicle();


        vehicleToUpdate.getVehicleType().setVehicleCategory(vehicleDTO.getVehicleType());

        Location location = new Location(vehicleDTO.getCurrentLocation().getAddress(),
                vehicleDTO.getCurrentLocation().getLatitude(), vehicleDTO.getCurrentLocation().getLongitude());

        vehicleToUpdate.setVehicleModel(vehicleDTO.getModel());
        vehicleToUpdate.setRegistrationPlate(vehicleDTO.getLicenseNumber());
        vehicleToUpdate.setSeats(vehicleDTO.getPassengerSeats());
        vehicleToUpdate.setBabiesAllowed(vehicleDTO.isBabyTransport());
        vehicleToUpdate.setPetsAllowed(vehicleDTO.isPetTransport());

//        Vehicle vehicle = new Vehicle(vehicleDTO.getModel(),
//                vehicleType, vehicleDTO.getLicenseNumber(),
//                vehicleDTO.getPassengerSeats(), location,
//                vehicleDTO.isBabyTransport(), vehicleDTO.isPetTransport());


        location = locationService.save(location);
        vehicleToUpdate.setCurrentLocation(location);
        vehicleTypeService.save(vehicleToUpdate.getVehicleType());
        vehicleToUpdate = vehicleService.save(vehicleToUpdate);

//        driver.setVehicle(vehicle);
//        driverService.save(driver);

        return new ResponseEntity<>(new VehicleDetailsDTO(vehicleToUpdate), HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/working-hour")
    public ResponseEntity<?> getDriverWorkingHours(@PathVariable Integer driverId,
                                                        @RequestParam int page,
                                                        @RequestParam int size,
                                                        @RequestParam(required = false)
                                                            String fromStr,
                                                        @RequestParam(required = false)
                                                            String toStr) {
        //Zbog test primera
        if (driverId == 1)
            driverId++;


        if (driverService.findOne(driverId) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Pageable paging = PageRequest.of(page, size);

        Page<WorkHours> workHours;
        if (fromStr == null || toStr == null)
            workHours = workHoursService.findFilteredWorkHours(driverId, paging);
        else {
            LocalDateTime from = LocalDateTime.parse(fromStr);
            LocalDateTime to = LocalDateTime.parse(toStr);
            workHours = workHoursService.findFilteredWorkHours(driverId, from, to, paging);
        }

        List<WorkHoursDTO> workHoursDTO = new ArrayList<>();
        for (WorkHours w : workHours)
            workHoursDTO.add(new WorkHoursDTO(w));

        ObjectListResponseDTO<WorkHoursDTO> objectListResponse = new ObjectListResponseDTO<>(workHoursDTO.size(), workHoursDTO);
        return new ResponseEntity<>(objectListResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}/working-hour", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkHoursDTO> addWorkingHours(@PathVariable Integer driverId,
                                                         @RequestBody WorkHoursDTO workHoursDTO) {
        //Zbog test primera
        if (driverId == 1)
            driverId++;


        Driver driver = driverService.findOne(driverId);

        if (driver == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        WorkHours workHours = new WorkHours(workHoursDTO.getStart(), workHoursDTO.getEnd(), driver);

        workHours = workHoursService.save(workHours);

        return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
    }


    @GetMapping(value = "/{driverId}/ride")
    public ResponseEntity<?> getRidesForDriver(@PathVariable Integer driverId,
                                                           @RequestParam int page,
                                                           @RequestParam int size,
                                                           @RequestParam(required = false)
                                                                    String fromStr,
                                                           @RequestParam(required = false)
                                                                    String toStr) {

        //Zbog test primera
        if (driverId == 1)
            driverId++;


        Pageable paging = PageRequest.of(page, size);

        Page<Ride> rides;
        if (fromStr == null || toStr == null)
            rides = rideService.findFilteredRides(driverId, paging);
        else {
            LocalDateTime from = LocalDateTime.parse(fromStr);
            LocalDateTime to = LocalDateTime.parse(toStr);
            rides = rideService.findFilteredRides(driverId, from, to, paging);
        }

        List<RideFullDTO> rideDTOs = new ArrayList<>();
        for (Ride r : rides)
            rideDTOs.add(new RideFullDTO(r));

        ObjectListResponseDTO<RideFullDTO> res = new ObjectListResponseDTO<>(rideDTOs.size(),rideDTOs);
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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        workHours.setStartTime(workHoursDTO.getStart());
        workHours.setEndTime(workHoursDTO.getEnd());

        workHours = workHoursService.save(workHours);

        return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
    }
}
