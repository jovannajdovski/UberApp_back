package com.uberTim12.ihor.controller;

import com.uberTim12.ihor.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;
}
