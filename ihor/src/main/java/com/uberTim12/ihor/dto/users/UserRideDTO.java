package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRideDTO {
    private Integer id;
    private String email;

    public UserRideDTO(User user){
        this(user.getId(), user.getEmail());
    }
}
