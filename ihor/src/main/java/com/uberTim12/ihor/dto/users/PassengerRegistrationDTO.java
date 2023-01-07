package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.util.ImageConverter;
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
        this(passenger.getName(), passenger.getSurname(), ImageConverter.encodeToString(passenger.getProfilePicture()),
                passenger.getTelephoneNumber(), passenger.getEmail(), passenger.getAddress(), passenger.getPassword());
    }

    public Passenger generatePassenger(){
        Passenger passenger = new Passenger();
        passenger.setName(this.name);
        passenger.setSurname(this.surname);
        passenger.setProfilePicture(ImageConverter.decodeToImage(this.profilePicture));
        passenger.setTelephoneNumber(this.telephoneNumber);
        passenger.setEmail(this.email);
        passenger.setAddress(this.address);
        passenger.setPassword(this.password);
        return passenger;
    }
}
