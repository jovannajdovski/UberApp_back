package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.util.ImageConverter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminRegistrationDTO {
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;

    private String profilePicture;
    @Pattern(regexp = "[0-9]+[0-9 \\-]+")
    private String telephoneNumber;

    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;
    @NotEmpty
    private String address;
    @Length(min = 6)
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
