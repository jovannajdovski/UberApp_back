package com.uberTim12.ihor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.InheritanceType.TABLE_PER_CLASS;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Inheritance(strategy=TABLE_PER_CLASS)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer id;

    private String name;

    private String surname;

    private String profilePicture;

    private String telephoneNumber;

    private String email;

    private String address;

    private String password;
}
