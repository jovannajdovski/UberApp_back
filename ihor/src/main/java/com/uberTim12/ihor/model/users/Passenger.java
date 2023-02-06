package com.uberTim12.ihor.model.users;

import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.ride.Ride;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Passenger extends User {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(
            name="passenger_ride",
            joinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id")
    )
    Set<Ride> rides = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(
            name="passenger_favorite",
            joinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "favorite_id", referencedColumnName = "id")
    )
    Set<Favorite> favoriteRoutes = new HashSet<>();

    public Passenger(String name, String surname, byte[] profilePicture, String telephoneNumber, String email, String address, String password, Authority authority, Set<Note> notes, boolean isBlocked, boolean isActive, Set<Ride> rides, Set<Favorite> favoriteRoutes) {
        super(name, surname, profilePicture, telephoneNumber, email, address, password, authority, notes, isBlocked, isActive);
        this.rides = rides;
        this.favoriteRoutes = favoriteRoutes;
    }
}
