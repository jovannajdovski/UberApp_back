package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.User;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRideDTO {
    private Integer id;
    private String email;

    public UserRideDTO(User user){
        this(user.getId(), user.getEmail());
    }
}
