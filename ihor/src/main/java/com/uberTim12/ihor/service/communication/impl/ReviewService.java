package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.dto.communication.*;
import com.uberTim12.ihor.model.communication.*;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.repository.communication.IReviewRepository;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.communication.interfaces.IReviewService;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService implements IReviewService {
    @Autowired
    private IReviewRepository reviewRepository;
    @Autowired
    private IRideRepository rideRepository;

    @Autowired
    private IPassengerRepository passengerRepository;

    @Override
    public NoteDTO createNote(Integer id, RequestNoteDTO requestNoteDTO) {
        return new NoteDTO(1, LocalDateTime.now(),requestNoteDTO.getMessage());
    }

    @Override
    public Page<NoteDTO> getNotes(Integer id, Pageable pageable) {
        List<NoteDTO> notes=new ArrayList<>();
        notes.add(new NoteDTO(1,LocalDateTime.now(),"prva poruka"));
        notes.add(new NoteDTO(2,LocalDateTime.now(),"druga poruka"));
        notes.add(new NoteDTO(3,LocalDateTime.now(),"treca poruka"));
        int from=pageable.getPageSize()* pageable.getPageNumber(), to=from+ pageable.getPageSize();
        if(from>notes.size()) from=notes.size();
        if(to> notes.size()) to=notes.size();
        return new PageImpl<>(notes.subList(from, to));
    }

    @Override
    public ReviewDTO createVehicleReview(Integer rideId, ReviewRequestDTO reviewRequestDTO) {
        Optional<Ride> ride=rideRepository.findById(rideId);
        if(ride.isEmpty()) return null;
        else {

            Review review=reviewRepository.findByRide(ride.get());
            if(review!=null) {
                review.setVehicleRate(reviewRequestDTO.getRating());
                review.setVehicleComment(reviewRequestDTO.getComment());

            }
            else{
                review = new Review(
                        reviewRequestDTO.getRating(),
                        reviewRequestDTO.getComment(),
                        null,
                        null,
                        passengerRepository.findById(1).get(),
                        ride.get()
                );//TODO losa specifikacija
            }
            reviewRepository.saveAndFlush(review);
            return new ReviewDTO(review,true);
        }
    }

    @Override
    public ReviewDTO createDriverReview(Integer rideId, ReviewRequestDTO reviewRequestDTO) {
        Optional<Ride> ride=rideRepository.findById(rideId);
        if(ride.isEmpty()) return null;
        else {
            Review review=reviewRepository.findByRide(ride.get());
            if(review!=null) {
                review.setDriverRate(reviewRequestDTO.getRating());
                review.setDriverComment(reviewRequestDTO.getComment());

            }
            else{
                review = new Review(
                        null,
                        null,
                        reviewRequestDTO.getRating(),
                        reviewRequestDTO.getComment(),
                        passengerRepository.findById(1).get(),
                        ride.get()
                );//TODO losa specifikacija
            }
            reviewRepository.saveAndFlush(review);
            return new ReviewDTO(review,false);
        }
    }

    @Override
    public List<ReviewDTO> getReviewsForVehicle(Integer vehicleId) {
        List<Review> reviews = reviewRepository.getReviewsForVehicle(vehicleId);
        return reviews.stream().map(r->new ReviewDTO(r,true)).collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> getReviewsForDriver(Integer driverId) {
        List<Review> reviews = reviewRepository.getReviewsForDriver(driverId);
        return reviews.stream().map(r->new ReviewDTO(r,false)).collect(Collectors.toList());
    }

    @Override
    public List<FullReviewDTO> getReviewsForRide(Integer rideId) {
        List<Review> reviews = reviewRepository.getReviewsForRide(rideId);
        return reviews.stream().map(FullReviewDTO::new).collect(Collectors.toList());
    }
}
