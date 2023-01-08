package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.users.*;
import com.uberTim12.ihor.dto.vehicle.VehicleAddDTO;
import com.uberTim12.ihor.dto.vehicle.VehicleDTO;
import com.uberTim12.ihor.dto.vehicle.VehicleDetailsDTO;
import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.exception.ShiftAlreadyStartedException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.DriverDocument;
import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.impl.DriverDocumentService;
import com.uberTim12.ihor.service.users.impl.DriverService;
import com.uberTim12.ihor.service.users.impl.WorkHoursService;
import com.uberTim12.ihor.service.users.interfaces.IDriverDocumentService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import com.uberTim12.ihor.service.vehicle.impl.VehicleService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import jakarta.persistence.EntityNotFoundException;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleTypeService;
import com.uberTim12.ihor.util.ImageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDetailsDTO> createDriver(@RequestBody DriverRegistrationDTO driverDTO) {
        Driver driver = new Driver(driverDTO.getName(),
                driverDTO.getSurname(),
                ImageConverter.decodeToImage(driverDTO.getProfilePicture()),
                driverDTO.getTelephoneNumber(),
                driverDTO.getEmail(),
                driverDTO.getAddress(),
                driverDTO.getPassword());

        try {
            driverService.register(driver);
            return new ResponseEntity<>(new DriverDetailsDTO(driver), HttpStatus.OK);
        } catch (EmailAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ObjectListResponseDTO<DriverDetailsDTO>> getDriversPage(Pageable page) {
        Page<Driver> drivers = driverService.getAll(page);

        List<DriverDetailsDTO> driverDTOs = new ArrayList<>();
        for (Driver d : drivers) {
            driverDTOs.add(new DriverDetailsDTO(d));
        }

        ObjectListResponseDTO<DriverDetailsDTO> objectListResponse = new ObjectListResponseDTO<>(driverService.getAll().size(), driverDTOs);
        return new ResponseEntity<>(objectListResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DriverDetailsDTO> getDriverDetails(@PathVariable Integer id) {
        try {
            Driver driver = driverService.get(id);
            return new ResponseEntity<>(new DriverDetailsDTO(driver), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDTO> updateDriver(@RequestBody DriverRegistrationDTO driverDTO, @PathVariable Integer id) {
        try {
            Driver driver = driverService.update(id, driverDTO.getName(), driverDTO.getSurname(),
                    driverDTO.getProfilePicture(), driverDTO.getTelephoneNumber(), driverDTO.getEmail(),
                    driverDTO.getAddress(), driverDTO.getPassword());
            return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
    }

    @GetMapping(value = "/{driverId}/documents")
    public ResponseEntity<List<DriverDocumentDTO>> getDriverDocuments(@PathVariable Integer driverId) {
        try {
            List<DriverDocument> documents = driverDocumentService.getDocumentsFor(driverId);
            List<DriverDocumentDTO> documentsDTO = new ArrayList<>();

            for (DriverDocument d : documents)
                documentsDTO.add(new DriverDocumentDTO(d));

            return new ResponseEntity<>(documentsDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
    }

    @PostMapping(value = "/{driverId}/documents")
    public ResponseEntity<DriverDocumentDTO> addDocumentToDriver(@PathVariable Integer driverId,
                                                                 @RequestBody DriverDocumentDetailsDTO driverDocumentDTO) {
        DriverDocument driverDocument = new DriverDocument(driverDocumentDTO.getName(),
                driverDocumentDTO.getDocumentImage(), null);

        try {
            driverDocumentService.addDocumentTo(driverId, driverDocument);
            return new ResponseEntity<>(new DriverDocumentDTO(driverDocument), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
    }


    @DeleteMapping(value = "/document/{documentId}")
    public ResponseEntity<String> deleteDocument(@PathVariable Integer documentId) {
        try {
            driverDocumentService.delete(documentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Driver document deleted successfully");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document does not exist!");
        }
    }

    @GetMapping(value = "/{driverId}/vehicle")
    public ResponseEntity<VehicleDetailsDTO> getDriverVehicle(@PathVariable Integer driverId) {
        try {
            Vehicle vehicle = vehicleService.getVehicleOf(driverId);
            return new ResponseEntity<>(new VehicleDetailsDTO(vehicle), HttpStatus.OK);
        } catch (EntityPropertyIsNullException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is not assigned!");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
    }

    @PostMapping(value = "/{driverId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDetailsDTO> addVehicleToDriver(@PathVariable Integer driverId,
                                                @RequestBody VehicleAddDTO vehicleDTO) {
        VehicleType vehicleType = new VehicleType(vehicleDTO.getVehicleType(), 10.0);
        Location location = new Location(vehicleDTO.getCurrentLocation().getAddress(),
                vehicleDTO.getCurrentLocation().getLatitude(), vehicleDTO.getCurrentLocation().getLongitude());
        Vehicle vehicle = new Vehicle(vehicleDTO.getModel(),
                vehicleType, vehicleDTO.getLicenseNumber(),
                vehicleDTO.getPassengerSeats(), location,
                vehicleDTO.isBabyTransport(), vehicleDTO.isPetTransport());

        try {
            vehicleService.addVehicleToDriver(driverId, vehicle);
            return new ResponseEntity<>(new VehicleDetailsDTO(vehicle), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
    }

    @PutMapping(value = "/{driverId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDetailsDTO> updateDriverVehicle(@PathVariable Integer driverId,
                                                 @RequestBody VehicleDTO vehicleDTO) {
        Location location = new Location(vehicleDTO.getCurrentLocation().getAddress(),
                vehicleDTO.getCurrentLocation().getLatitude(), vehicleDTO.getCurrentLocation().getLongitude());

        try {
            Vehicle vehicle = vehicleService.updateVehicleForDriver(driverId, vehicleDTO.getVehicleType(), vehicleDTO.getModel(),
                    vehicleDTO.getLicenseNumber(), location, vehicleDTO.getPassengerSeats(), vehicleDTO.isBabyTransport(),
                    vehicleDTO.isPetTransport());
            return new ResponseEntity<>(new VehicleDetailsDTO(vehicle), HttpStatus.OK);
        } catch (EntityPropertyIsNullException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver does not have vehicle assigned!");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }

    }

    @GetMapping(value = "/{driverId}/working-hour")
    public ResponseEntity<ObjectListResponseDTO<WorkHoursDTO>> getDriverWorkingHours(@PathVariable Integer driverId,
                                                   @RequestParam int page,
                                                   @RequestParam int size,
                                                   @RequestParam(required = false)
                                                   String fromStr,
                                                   @RequestParam(required = false)
                                                   String toStr) {
        try {
            driverService.get(driverId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }

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

    @PostMapping(value = "/{driverId}/working-hour", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkHoursDTO> addWorkingHours(@PathVariable Integer driverId,
                                                        @RequestBody WorkHoursDTO workHoursDTO) {
        WorkHours workHours = new WorkHours(workHoursDTO.getStart(), workHoursDTO.getEnd(), null);

        try {
            workHoursService.startShift(driverId, workHours);
            return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        } catch (EntityPropertyIsNullException | ShiftAlreadyStartedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @GetMapping(value = "/{driverId}/ride")
    public ResponseEntity<ObjectListResponseDTO<RideFullDTO>> getRidesForDriver(@PathVariable Integer driverId,
                                               @RequestParam int page,
                                               @RequestParam int size,
                                               @RequestParam(required = false)
                                               String fromStr,
                                               @RequestParam(required = false)
                                               String toStr) {
        try {
            driverService.get(driverId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }

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

        ObjectListResponseDTO<RideFullDTO> res = new ObjectListResponseDTO<>(rideDTOs.size(), rideDTOs);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/working-hour/{workingHourId}")
    public ResponseEntity<WorkHoursDTO> getWorkingHours(@PathVariable Integer workingHourId) {
        try {
            WorkHours workHours = workHoursService.get(workingHourId);
            return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Working hour does not exist!");
        }
    }

    @PutMapping(value = "/working-hour/{workingHourId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkHoursDTO> changeDriverWorkingHours(@PathVariable Integer workingHourId,
                                                      @RequestBody WorkHoursDTO workHoursDTO) {
        try {
            WorkHours workHours = workHoursService.endShift(workingHourId, workHoursDTO.getEnd());
            return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Working hour does not exist!");
        }
    }
}
