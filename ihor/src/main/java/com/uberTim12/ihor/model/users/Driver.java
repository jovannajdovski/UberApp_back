package com.uberTim12.ihor.model.users;

import com.uberTim12.ihor.model.communication.Review;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Driver extends User{

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DriverDocument> documents = new HashSet<>();

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Ride> rides = new HashSet<>();

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private Vehicle vehicle;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();

    public Driver(String name, String surname, String profilePicture, String telephoneNumber, String email, String address, String password) {
        super(name, surname, profilePicture, telephoneNumber, email, address, password);
    }

}
