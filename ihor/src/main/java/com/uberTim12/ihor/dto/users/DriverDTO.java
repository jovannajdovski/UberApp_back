package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.DriverDocument;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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

    public DriverDTO(Driver driver)
    {
        this(driver.getId(),
                driver.getName(),
                driver.getSurname(),
                driver.getProfilePicture(),
                driver.getTelephoneNumber(),
                driver.getEmail(),
                driver.getAddress()
        );
    }
}
