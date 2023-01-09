package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.util.ImageConverter;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Integer id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
    public UserDTO(User user)
    {
        this(user.getId(),
                user.getName(),
                user.getSurname(),
                ImageConverter.encodeToString(user.getProfilePicture()),
                user.getTelephoneNumber(),
                user.getEmail(),
                user.getAddress());
    }
}
