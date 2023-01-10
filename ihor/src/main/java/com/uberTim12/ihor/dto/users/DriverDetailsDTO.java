package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.util.ImageConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DriverDetailsDTO {
    private Integer id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;

    public DriverDetailsDTO(Driver driver)
    {
        this(driver.getId(),
                driver.getName(),
                driver.getSurname(),
                ImageConverter.encodeToString(driver.getProfilePicture()),
                driver.getTelephoneNumber(),
                driver.getEmail(),
                driver.getAddress()
        );
    }
}
