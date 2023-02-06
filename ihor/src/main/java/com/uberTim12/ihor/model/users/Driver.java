package com.uberTim12.ihor.model.users;

import com.uberTim12.ihor.model.communication.Review;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Driver extends User{

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DriverDocument> documents = new HashSet<>();

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Ride> rides = new HashSet<>();

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL)
    private Vehicle vehicle;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();

    public Driver(String name, String surname, byte[] profilePicture, String telephoneNumber, String email, String address, String password) {
        super(name, surname, profilePicture, telephoneNumber, email, address, password);
    }

    public Driver(String name, String surname, byte[] profilePicture, String telephoneNumber, String email, String address, String password, Authority authority, Set<Note> notes, boolean isBlocked, boolean isActive, Set<DriverDocument> documents, Set<Ride> rides, Vehicle vehicle, Set<Review> reviews) {
        super(name, surname, profilePicture, telephoneNumber, email, address, password, authority, notes, isBlocked, isActive);
        this.documents = documents;
        this.rides = rides;
        this.vehicle = vehicle;
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "documents=" + documents.size() +
                ", rides=" + rides.size() +
                ", vehicle=" + vehicle.getId() +
                ", reviews=" + reviews.size() +
                '}';
    }
}
