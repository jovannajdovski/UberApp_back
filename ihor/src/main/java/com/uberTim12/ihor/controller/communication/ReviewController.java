package com.uberTim12.ihor.controller.communication;

import com.uberTim12.ihor.dto.communication.FullReviewDTO;
import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.communication.ReviewDTO;
import com.uberTim12.ihor.dto.communication.ReviewRequestDTO;
import com.uberTim12.ihor.model.communication.Review;
import com.uberTim12.ihor.service.communication.impl.ReviewService;
import com.uberTim12.ihor.service.communication.interfaces.IReviewService;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/review")
public class ReviewController {
    private final IReviewService reviewService;
    private final IDriverService driverService;
    private final IVehicleService vehicleService;
    private final IRideService rideService;
    

    @Autowired
    public ReviewController(ReviewService reviewService, IDriverService driverService, IVehicleService vehicleService, RideService rideService) {
        this.reviewService = reviewService;
        this.driverService = driverService;
        this.vehicleService = vehicleService;
        this.rideService = rideService;
    }

    @PostMapping(value = "/{rideId}/vehicle",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<ReviewDTO> leaveReviewForVehicle(@PathVariable Integer rideId, @RequestBody ReviewRequestDTO reviewRequestDTO)
    {
        try {
            Review review = reviewService.createVehicleReview(rideId, reviewRequestDTO.getRating(), reviewRequestDTO.getComment());
            return new ResponseEntity<>(new ReviewDTO(review, true), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
    }

    @GetMapping(value = "/vehicle/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObjectListResponseDTO<ReviewDTO>> getReviewsForVehicle(@PathVariable("id") Integer vehicleId)
    {
        try {
            vehicleService.get(vehicleId);
            List<Review> reviews = reviewService.getReviewsForVehicle(vehicleId);

            List<ReviewDTO> reviewDTOs = new ArrayList<>();
            for(Review r : reviews)
                reviewDTOs.add(new ReviewDTO(r, true));

            ObjectListResponseDTO<ReviewDTO> res = new ObjectListResponseDTO<>(reviewDTOs.size(), reviewDTOs);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle does not exist!");
        }
    }

    @PostMapping(value = "/{rideId}/driver",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<ReviewDTO> leaveReviewForDriver(@PathVariable Integer rideId, @RequestBody ReviewRequestDTO reviewRequestDTO)
    {
        try {
            Review review = reviewService.createDriverReview(rideId, reviewRequestDTO.getRating(), reviewRequestDTO.getComment());
            return new ResponseEntity<>(new ReviewDTO(review, false), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
    }

    @GetMapping(value = "/driver/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObjectListResponseDTO<ReviewDTO>> getReviewsForDriver(@PathVariable("id") Integer driverId)
    {
        try {
            driverService.get(driverId);
            List<Review> reviews = reviewService.getReviewsForDriver(driverId);

            List<ReviewDTO> reviewDTOs = new ArrayList<>();
            for(Review r : reviews)
                reviewDTOs.add(new ReviewDTO(r, false));

            ObjectListResponseDTO<ReviewDTO> res = new ObjectListResponseDTO<>(reviewDTOs.size(), reviewDTOs);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }

    }

    @GetMapping(value = "/{rideId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FullReviewDTO>> getReviewsForRide(@PathVariable Integer rideId)
    {
        try {
            rideService.get(rideId);
            List<Review> reviews = reviewService.getReviewsForRide(rideId);

            List<FullReviewDTO> reviewDTOs = new ArrayList<>();
            for(Review r : reviews)
                reviewDTOs.add(new FullReviewDTO(r));

            return new ResponseEntity<>(reviewDTOs, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
    }

}
