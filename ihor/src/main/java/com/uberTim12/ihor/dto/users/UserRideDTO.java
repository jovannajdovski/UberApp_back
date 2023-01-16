package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRideDTO {
    @Min(value = 1)
    private Integer id;
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;

    public UserRideDTO(User user){
        this(user.getId(), user.getEmail());
    }
}
