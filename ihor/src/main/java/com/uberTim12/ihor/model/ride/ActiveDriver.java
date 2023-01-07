package com.uberTim12.ihor.model.ride;

import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.Driver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ActiveDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.REFRESH)
    private Driver driver;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Location location;
}
