package com.uberTim12.ihor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
public class Driver extends User{

    @OneToMany
    private Set<DriverDocument> documents;

    @OneToMany
    private Set<Ride> rides;

    @OneToOne
    private Vehicle vehicle;
}
