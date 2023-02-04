package com.uberTim12.ihor.timer;

import com.uberTim12.ihor.dto.route.LocationDTO;
import com.uberTim12.ihor.dto.users.NumberDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.service.users.interfaces.IWorkHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.TimerTask;

@Component
public class WorkHoursTimer extends TimerTask {
    SimpMessagingTemplate simpMessagingTemplate;
    public final IWorkHoursService workHoursService;

    int driverId;
    @Autowired
    public WorkHoursTimer(IWorkHoursService workHoursService) {
        this.workHoursService = workHoursService;
    }
    public void setProperties(int driverId, SimpMessagingTemplate simpMessagingTemplate)
    {
        this.driverId=driverId;
        this.simpMessagingTemplate=simpMessagingTemplate;
    }

    @Override
    public void run() {
        Integer remainedMinutes=480-workHoursService.getWorkingMinutesByDriverAtChosenDay(driverId, LocalDate.now());
        this.simpMessagingTemplate.convertAndSend("api/socket-publisher/" +driverId+"/work-hours" , new NumberDTO(remainedMinutes));

    }
}
