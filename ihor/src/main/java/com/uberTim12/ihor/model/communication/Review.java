package com.uberTim12.ihor.model.communication;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vehicle_rate", nullable = false)
    private Double vehicleRate;

    @Column(name = "vehicle_comment")
    private String vehicleComment;

    @Column(name = "driver_rate", nullable = false)
    private Double driverRate;

    @Column(name = "driver_comment")
    private String driverComment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ride_id")
    private Ride ride;

    public Review(Double vehicleRate, String vehicleComment, Double driverRate, String driverComment, Passenger passenger, Ride ride) {
        this.vehicleRate = vehicleRate;
        this.vehicleComment = vehicleComment;
        this.driverRate = driverRate;
        this.driverComment = driverComment;
        this.passenger = passenger;
        this.ride = ride;
    }
}
