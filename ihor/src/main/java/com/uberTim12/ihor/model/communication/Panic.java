package com.uberTim12.ihor.model.communication;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Panic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ride_id")
    private Ride currentRide;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "reason", nullable = false)
    private String reason;

}
