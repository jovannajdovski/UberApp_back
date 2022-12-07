package com.uberTim12.ihor.model.users;

import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.ride.Ride;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
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
            name="passenger_route",
            joinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "path_id", referencedColumnName = "id")
    )
    Set<Path> favoriteRoutes = new HashSet<>();
}
