package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.util.ImageConverter;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerRegistrationDTO {

    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    private String profilePicture;
    @Pattern(regexp = "[0-9 +]?[0-9]+[0-9 \\-]+")
    private String telephoneNumber;
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;
    @NotEmpty
    private String address;
    @Length(min = 6)
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
