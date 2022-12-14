package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverDTO {
    private Integer id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
    private String password;

    public DriverDTO(Driver driver)
    {
        this(driver.getId(),
                driver.getName(),
                driver.getSurname(),
                driver.getProfilePicture(),
                driver.getTelephoneNumber(),
                driver.getEmail(),
                driver.getAddress(),
                driver.getPassword()
        );
    }
}
