package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPanicDTO {

    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;

    public UserPanicDTO(User user){
        this(user.getName(), user.getSurname(), user.getProfilePicture(),
                user.getTelephoneNumber(), user.getEmail(), user.getAddress());
    }
}
