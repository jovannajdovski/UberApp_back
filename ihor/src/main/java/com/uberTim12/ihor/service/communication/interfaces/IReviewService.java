package com.uberTim12.ihor.service.communication.interfaces;

import com.uberTim12.ihor.dto.communication.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IReviewService {
    NoteDTO createNote(Integer id, RequestNoteDTO requestNoteDTO);

    Page<NoteDTO> getNotes(Integer id);

    ReviewDTO createVehicleReview(Integer rideId, Integer vehicleId, ReviewRequestDTO reviewRequestDTO);

    ReviewDTO createDriverReview(Integer rideId, Integer driverId, ReviewRequestDTO reviewRequestDTO);

    List<ReviewDTO> getReviewsForVehicle(Integer vehicleId);

    List<ReviewDTO> getReviewsForDriver(Integer driverId);

    List<FullReviewDTO> getReviewsForRide(Integer rideId);
}
