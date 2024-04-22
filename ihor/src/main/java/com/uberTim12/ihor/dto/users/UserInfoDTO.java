package com.uberTim12.ihor.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInfoDTO {
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
}
