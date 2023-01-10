package com.uberTim12.ihor.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserTokensDTO {
    private String accessToken;
    private String refreshToken;
}
