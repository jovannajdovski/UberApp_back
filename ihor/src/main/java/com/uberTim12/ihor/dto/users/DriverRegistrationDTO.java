package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.util.ImageConverter;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DriverRegistrationDTO {
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
    private String password;

    public DriverRegistrationDTO(Driver driver)
    {
        this(driver.getName(),
                driver.getSurname(),
                ImageConverter.encodeToString(driver.getProfilePicture()),
                driver.getTelephoneNumber(),
                driver.getEmail(),
                driver.getAddress(),
                driver.getPassword()
        );
    }
}
