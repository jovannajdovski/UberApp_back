package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.ResponseMessageDTO;
import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.stats.*;
import com.uberTim12.ihor.dto.users.*;
import com.uberTim12.ihor.dto.vehicle.VehicleAddDTO;
import com.uberTim12.ihor.dto.vehicle.VehicleDTO;
import com.uberTim12.ihor.dto.vehicle.VehicleDetailsDTO;
import com.uberTim12.ihor.exception.*;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.stats.DriverStatistics;
import com.uberTim12.ihor.model.stats.RideDistanceStatistics;
import com.uberTim12.ihor.model.stats.RideCountStatistics;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.DriverDocument;
import com.uberTim12.ihor.model.users.WorkHours;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import com.uberTim12.ihor.security.AuthUtil;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.stats.interfaces.IDriverStatisticsService;
import com.uberTim12.ihor.service.users.impl.DriverDocumentService;
import com.uberTim12.ihor.service.users.impl.DriverService;
import com.uberTim12.ihor.service.users.impl.WorkHoursService;
import com.uberTim12.ihor.service.users.interfaces.IDriverDocumentService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import com.uberTim12.ihor.service.vehicle.impl.VehicleService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import com.uberTim12.ihor.util.ImageConverter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(value = "api/driver")
public class DriverController {
    private final IDriverService driverService;
    private final IDriverDocumentService driverDocumentService;
    private final IVehicleService vehicleService;
    private final IWorkHoursService workHoursService;
    private final IRideService rideService;
    private final IDriverStatisticsService driverStatisticsService;
    private final JwtUtil jwtUtil;

    @Autowired
    DriverController(DriverService driverService,
                     DriverDocumentService driverDocumentService,
                     VehicleService vehicleService,
                     WorkHoursService workHoursService,
                     RideService rideService, IDriverStatisticsService driverStatisticsService, AuthUtil authUtil, JwtUtil jwtUtil) {
        this.driverService = driverService;
        this.driverDocumentService = driverDocumentService;
        this.vehicleService = vehicleService;
        this.workHoursService = workHoursService;
        this.rideService = rideService;
        this.driverStatisticsService = driverStatisticsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDriver(@Valid @RequestBody DriverRegistrationDTO driverDTO) {

        Driver driver = new Driver(driverDTO.getName(),
                driverDTO.getSurname(),
                ImageConverter.decodeToImage(driverDTO.getProfilePicture()),
                driverDTO.getTelephoneNumber(),
                driverDTO.getEmail(),
                driverDTO.getAddress(),
                driverDTO.getPassword());

        try {
            driver = driverService.register(driver);
            return new ResponseEntity<>(new DriverDetailsDTO(driver), HttpStatus.OK);
        } catch (EmailAlreadyExistsException e) {
            return new ResponseEntity<>(new ResponseMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDriversPage(Pageable page) {

        Page<Driver> drivers = driverService.getAll(page);

        List<DriverDetailsDTO> driverDTOs = new ArrayList<>();
        for (Driver d : drivers) {
            driverDTOs.add(new DriverDetailsDTO(d));
        }

        ObjectListResponseDTO<DriverDetailsDTO> objectListResponse = new ObjectListResponseDTO<>((int) drivers.getTotalElements(), driverDTOs);
        return new ResponseEntity<>(objectListResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDriverDetails(@Min(value = 1) @PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        try {
            Driver driver = driverService.get(id);
            return new ResponseEntity<>(new DriverDetailsDTO(driver), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> updateDriver(@Valid @RequestBody DriverRegistrationDTO driverDTO, @Min(value = 1) @PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            String iddd = jwtUtil.extractId(jwtToken);
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(id)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        try {
            Driver driver = driverService.update(id, driverDTO.getName(), driverDTO.getSurname(),
                    driverDTO.getProfilePicture(), driverDTO.getTelephoneNumber(), driverDTO.getEmail(),
                    driverDTO.getAddress(), driverDTO.getPassword());
            return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{driverId}/documents")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getDriverDocuments(@Min(value = 1) @PathVariable Integer driverId, @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        try {
            List<DriverDocument> documents = driverDocumentService.getDocumentsFor(driverId);
            List<DriverDocumentDTO> documentsDTO = new ArrayList<>();

            for (DriverDocument d : documents)
                documentsDTO.add(new DriverDocumentDTO(d));

            return new ResponseEntity<>(documentsDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{driverId}/documents")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> addDocumentToDriver(@Min(value = 1) @PathVariable Integer driverId,
                                                 @Valid @RequestBody DriverDocumentDetailsDTO driverDocumentDTO,
                                                 @RequestHeader("Authorization") String authHeader) {

        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        DriverDocument driverDocument = new DriverDocument(driverDocumentDTO.getName(),
                driverDocumentDTO.getDocumentImage(), null);

        try {
            driverDocumentService.addDocumentTo(driverId, driverDocument);
            return new ResponseEntity<>(new DriverDocumentDTO(driverDocument), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping(value = "/document/{documentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> deleteDocument(@Min(value = 1) @PathVariable Integer documentId, @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            try {
                DriverDocument document = driverDocumentService.get(documentId);
                if (!Objects.equals(document.getDriver().getId(), loggedId)) {
                    return new ResponseEntity<>("Document does not exist!", HttpStatus.NOT_FOUND);
                }
            } catch (EntityNotFoundException e) {
                return new ResponseEntity<>("Document does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        try {
            driverDocumentService.delete(documentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Driver document deleted successfully");
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Document does not exist!", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{driverId}/vehicle")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getDriverVehicle(@Min(value = 1) @PathVariable Integer driverId, @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        try {
            Vehicle vehicle = vehicleService.getVehicleOf(driverId);
            return new ResponseEntity<>(new VehicleDetailsDTO(vehicle), HttpStatus.OK);
        } catch (EntityPropertyIsNullException e) {
            return new ResponseEntity<>(new ResponseMessageDTO("Vehicle is not assigned!"), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{driverId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> addVehicleToDriver(@Min(value = 1) @PathVariable Integer driverId,
                                                @Valid @RequestBody VehicleAddDTO vehicleDTO, @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

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
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{driverId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> updateDriverVehicle(@Min(value = 1) @PathVariable Integer driverId,
                                                 @Valid @RequestBody VehicleDTO vehicleDTO,
                                                 @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        Location location = new Location(vehicleDTO.getCurrentLocation().getAddress(),
                vehicleDTO.getCurrentLocation().getLatitude(), vehicleDTO.getCurrentLocation().getLongitude());

        try {
            Vehicle vehicle = vehicleService.updateVehicleForDriver(driverId, vehicleDTO.getVehicleType(), vehicleDTO.getModel(),
                    vehicleDTO.getLicenseNumber(), location, vehicleDTO.getPassengerSeats(), vehicleDTO.isBabyTransport(),
                    vehicleDTO.isPetTransport());
            return new ResponseEntity<>(new VehicleDetailsDTO(vehicle), HttpStatus.OK);
        } catch (EntityPropertyIsNullException e) {
            return new ResponseEntity<>(new ResponseMessageDTO("Driver does not have vehicle assigned!"), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping(value = "/{driverId}/working-hour")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getDriverWorkingHours(@Min(value = 1) @PathVariable Integer driverId,
                                                   @Min(value = 0) @RequestParam int page,
                                                   @Min(value = 1) @RequestParam int size,
                                                   @RequestParam(required = false)
                                                   String fromStr,
                                                   @RequestParam(required = false)
                                                   String toStr,
                                                   @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        try {
            driverService.get(driverId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
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

        ObjectListResponseDTO<WorkHoursDTO> objectListResponse = new ObjectListResponseDTO<>((int) workHours.getTotalElements(), workHoursDTO);
        return new ResponseEntity<>(objectListResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/{driverId}/working-hour", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> addWorkingHours(@Min(value = 1) @PathVariable Integer driverId,
                                             @Valid @RequestBody WorkHoursStartDTO workHoursDTO,
                                             @RequestHeader("Authorization") String authHeader) {

        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        WorkHours workHours = new WorkHours(workHoursDTO.getStart());

        try {
            workHoursService.startShift(driverId, workHours);
            return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        } catch (EntityPropertyIsNullException | ShiftAlreadyStartedException | WorkTimeExceededException e) {
            return new ResponseEntity<>(new ResponseMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/{driverId}/ride")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getRidesForDriver(@Min(value = 1) @PathVariable Integer driverId,
                                               @Min(value = 0) @RequestParam int page,
                                               @Min(value = 1) @RequestParam int size,
                                               @RequestParam(required = false)
                                               String fromStr,
                                               @RequestParam(required = false)
                                               String toStr,
                                               @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        try {
            driverService.get(driverId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
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

        ObjectListResponseDTO<RideFullDTO> res = new ObjectListResponseDTO<>((int) rides.getTotalElements(), rideDTOs);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/working-hour/{workingHourId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getWorkingHours(@Min(value = 1) @PathVariable Integer workingHourId, @RequestHeader("Authorization") String authHeader) {

        String jwtToken = authHeader.substring(7);

        try {
            WorkHours workHours = workHoursService.get(workingHourId);

            if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
                Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
                if (!loggedId.equals(workHours.getDriver().getId())) {
                    return new ResponseEntity<>("Working hour does not exist!", HttpStatus.NOT_FOUND);
                }
            }

            return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Working hour does not exist!", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/working-hour/{workingHourId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> changeDriverWorkingHours(@Min(value = 1) @PathVariable Integer workingHourId,
                                                      @Valid @RequestBody WorkHoursEndDTO workHoursDTO,
                                                      @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));

        try {
            WorkHours workHours = workHoursService.endShift(workingHourId, workHoursDTO.getEnd());
            if (!loggedId.equals(workHours.getDriver().getId())) {
                return new ResponseEntity<>("Working hour does not exist!", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(new WorkHoursDTO(workHours), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Working hour does not exist!", HttpStatus.NOT_FOUND);
        } catch (ShiftIsNotOngoingException e) {
            return new ResponseEntity<>(new ResponseMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (EntityPropertyIsNullException e) {
            return new ResponseEntity<>(new ResponseMessageDTO("Cannot end shift because the vehicle is not defined!"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{driverId}/ride/pending")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> getPendingRidesForDriver(@Min(value = 1) @PathVariable Integer driverId,
                                                      @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }
        try {
            driverService.get(driverId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }

        List<Ride> rides = rideService.findPendingRides(driverId);
        List<RideFullDTO> rideDTOs = new ArrayList<>();
        for (Ride r : rides)
            rideDTOs.add(new RideFullDTO(r));

        ObjectListResponseDTO<RideFullDTO> res = new ObjectListResponseDTO<>(rides.size(), rideDTOs);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getStatisticsForDriver(@Min(value = 1) @PathVariable Integer driverId,
                                                    @RequestHeader("Authorization") String authHeader,
                                                    @RequestBody TimeSpanDTO timeSpanDTO
                                                    ) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        try {
            driverService.get(driverId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }

        DriverStatistics statistics = driverStatisticsService.getDriverStatistics(driverId, timeSpanDTO.from,
                timeSpanDTO.to);
        return new ResponseEntity<>(new DriverStatisticsDTO(statistics),
                HttpStatus.OK);
    }


    @GetMapping(value = "/{driverId}/ride-count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getRideCountStatistics(@Min(value = 1) @PathVariable Integer driverId,
                                                         @RequestHeader("Authorization") String authHeader,
                                                         @RequestBody TimeSpanDTO timeSpanDTO) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        try {
            driverService.get(driverId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }

        RideCountStatistics statistics = driverStatisticsService.numberOfRidesStatistics(driverId, timeSpanDTO.from,
                timeSpanDTO.to);
        return new ResponseEntity<>(new RideCountStatisticsDTO(statistics), HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/distance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getDistanceStatistics(@Min(value = 1) @PathVariable Integer driverId,
                                                         @RequestHeader("Authorization") String authHeader,
                                                         @RequestBody TimeSpanDTO timeSpanDTO) {
        String jwtToken = authHeader.substring(7);
        if (!jwtUtil.extractRole(jwtToken).equals("ROLE_ADMIN")) {
            Integer loggedId = Integer.parseInt(jwtUtil.extractId(jwtToken));
            if (!loggedId.equals(driverId)) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        try {
            driverService.get(driverId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }

        RideDistanceStatistics statistics = driverStatisticsService.distancePerDayStatistics(driverId, timeSpanDTO.from, timeSpanDTO.to);
        return new ResponseEntity<>(new RideDistanceStatisticsDTO(statistics), HttpStatus.OK);
    }
}
