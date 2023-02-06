package com.uberTim12.ihor.model.users;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Set;


@Entity
public class Administrator extends User {
    public Administrator(String name, String surname, byte[] profilePicture, String telephoneNumber, String email, String address, String password, Authority authority, Set<Note> notes, boolean isBlocked, boolean isActive) {
        super(name, surname, profilePicture, telephoneNumber, email, address, password, authority, notes, isBlocked, isActive);
    }

    public Administrator() {

    }
}
