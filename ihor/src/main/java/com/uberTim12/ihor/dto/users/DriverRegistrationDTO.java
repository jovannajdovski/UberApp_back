package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Driver;
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
public class DriverRegistrationDTO {
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
//    @Length(min = 6)
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
