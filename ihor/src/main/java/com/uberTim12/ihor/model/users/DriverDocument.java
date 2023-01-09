package com.uberTim12.ihor.model.users;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class DriverDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "picture", nullable = false)
    private String picture;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    public DriverDocument(String name, String picture, Driver driver) {
        this.name = name;
        this.picture = picture;
        this.driver = driver;
    }
}
