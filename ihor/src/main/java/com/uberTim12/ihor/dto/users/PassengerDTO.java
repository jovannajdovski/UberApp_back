package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Passenger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerDTO {

    private Integer id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;

    public PassengerDTO(Passenger passenger){
        this(passenger.getId(), passenger.getName(), passenger.getSurname(), passenger.getProfilePicture(),
                passenger.getTelephoneNumber(), passenger.getEmail(), passenger.getAddress());
    }
}
