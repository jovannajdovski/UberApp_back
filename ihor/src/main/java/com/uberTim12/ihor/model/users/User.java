package com.uberTim12.ihor.model.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.InheritanceType.JOINED;
import static jakarta.persistence.InheritanceType.TABLE_PER_CLASS;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Inheritance(strategy=JOINED)
@Table(name = "ihor")
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "telephone_number")
    private String telephoneNumber;

    @Column(name = "email", nullable = false) //unique=true
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authority_id")
    private Authority authority;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name="is_active")
    private boolean isActive;


    protected User(String name, String surname, String profilePicture, String telephoneNumber, String email, String address, String password) {
        super();
        this.setName(name);
        this.setSurname(surname);
        this.setProfilePicture(profilePicture);
        this.setTelephoneNumber(telephoneNumber);
        this.setEmail(email);
        this.setAddress(address);
        this.setPassword(password);
        this.setBlocked(false);
        this.setActive(true);
    }
}
