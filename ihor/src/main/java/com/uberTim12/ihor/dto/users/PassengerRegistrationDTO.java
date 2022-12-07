package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Passenger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PassengerRegistrationDTO {

    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
    private String password;

    public PassengerRegistrationDTO(Passenger passenger){
        this(passenger.getName(), passenger.getSurname(), passenger.getProfilePicture(),
                passenger.getTelephoneNumber(), passenger.getEmail(), passenger.getAddress(), passenger.getPassword());
    }
}
