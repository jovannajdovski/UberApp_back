package com.uberTim12.ihor.controller.comunication;

import com.uberTim12.ihor.dto.comunication.PanicDTO;
import com.uberTim12.ihor.dto.comunication.PanicListResponseDTO;
import com.uberTim12.ihor.dto.ride.RideDTO;
import com.uberTim12.ihor.dto.users.PassengerDTO;
import com.uberTim12.ihor.model.comunication.Panic;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.service.comunication.impl.PanicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/panic")
public class PanicController {

    @Autowired
    private PanicService panicService;

    @GetMapping
    public ResponseEntity<?> getPanicNotifications() {

        List<Panic> panics = panicService.findAll();

        List<PanicDTO> panicsDTO = new ArrayList<>();
        for (Panic p : panics) {
            panicsDTO.add(new PanicDTO(p));
        }

        PanicListResponseDTO res = new PanicListResponseDTO(panicsDTO.size(),panicsDTO);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
