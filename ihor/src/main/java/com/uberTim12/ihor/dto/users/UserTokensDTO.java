package com.uberTim12.ihor.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserTokensDTO {
    private String accessToken;
    private String refreshToken;
}
