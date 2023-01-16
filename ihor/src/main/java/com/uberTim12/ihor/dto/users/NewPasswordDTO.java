package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Passenger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewPasswordDTO {
    @Length(min = 6)
    private String oldPassword;
    @Length(min = 6)
    private String newPassword;

}
