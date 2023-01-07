package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.util.ImageConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminRegistrationDTO {

    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
    private String password;

    public AdminRegistrationDTO(Administrator admin){
        this(admin.getName(), admin.getSurname(), ImageConverter.encodeToString(admin.getProfilePicture()),
                admin.getTelephoneNumber(), admin.getEmail(), admin.getAddress(), admin.getPassword());
    }

    public Administrator generateAdministrator(){
        Administrator admin = new Administrator();
        admin.setName(this.name);
        admin.setSurname(this.surname);
        admin.setProfilePicture(ImageConverter.decodeToImage(this.profilePicture));
        admin.setTelephoneNumber(this.telephoneNumber);
        admin.setEmail(this.email);
        admin.setAddress(this.address);
        admin.setPassword(this.password);
        return admin;
    }
}
