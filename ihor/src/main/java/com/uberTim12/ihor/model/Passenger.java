package com.uberTim12.ihor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Passenger extends User {

    @ManyToMany
    Set<Ride> rides;

    @ManyToMany
    Set<Path> favoriteRoutes;
}
