package com.uberTim12.ihor.dto.users;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResetPasswordDTO {

    private String code;
    private String new_password;

}
