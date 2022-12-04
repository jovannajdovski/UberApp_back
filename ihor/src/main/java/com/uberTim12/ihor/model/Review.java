package com.uberTim12.ihor.model;

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

    private Double rate;

    private String comment;

    @ManyToOne
    private Ride ride;

    @ManyToOne
    private Passenger passenger;
}
