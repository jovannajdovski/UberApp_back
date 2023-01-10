package com.uberTim12.ihor.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthTokenDTO {
    String accessToken;
    String refreshToken;
}
