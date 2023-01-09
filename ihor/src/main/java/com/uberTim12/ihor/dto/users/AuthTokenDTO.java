package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Authority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthTokenDTO {
    private String jwt;
    private String username;
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean loggedIn;
    private Authority authority;
}
