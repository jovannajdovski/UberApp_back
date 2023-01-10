package com.uberTim12.ihor.controller.communication;

import com.uberTim12.ihor.dto.communication.FullReviewDTO;
import com.uberTim12.ihor.dto.communication.ObjectListResponseDTO;
import com.uberTim12.ihor.dto.communication.ReviewDTO;
import com.uberTim12.ihor.dto.communication.ReviewRequestDTO;
import com.uberTim12.ihor.service.communication.impl.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping(value = "/{rideId}/vehicle",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> leaveReviewForVehicle(@PathVariable Integer rideId, @RequestBody ReviewRequestDTO reviewRequestDTO)
    {
        if(rideId==null) //TODO sve greske
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        else {
            ReviewDTO review = reviewService.createVehicleReview(rideId, reviewRequestDTO);
            if(review==null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>(review, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/vehicle/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReviewsForVehicle(@PathVariable("id") Integer vehicleId)
    {
        List<ReviewDTO> reviews=reviewService.getReviewsForVehicle(vehicleId);
        ObjectListResponseDTO<ReviewDTO> res = new ObjectListResponseDTO<>(reviews.size(),reviews);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping(value = "/{rideId}/driver",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> leaveReviewForDriver(@PathVariable Integer rideId, @RequestBody ReviewRequestDTO reviewRequestDTO)
    {
        if(rideId==null) //TODO sve greske
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        else {
            ReviewDTO review = reviewService.createDriverReview(rideId, reviewRequestDTO);
            if(review==null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>(review, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/driver/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReviewsForDriver(@PathVariable("id") Integer driverId)
    {
        List<ReviewDTO> reviews=reviewService.getReviewsForDriver(driverId);
        ObjectListResponseDTO<ReviewDTO> res = new ObjectListResponseDTO<>(reviews.size(),reviews);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/{rideId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReviewsForRide(@PathVariable Integer rideId)
    {
        List<FullReviewDTO> reviews=reviewService.getReviewsForRide(rideId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

}
