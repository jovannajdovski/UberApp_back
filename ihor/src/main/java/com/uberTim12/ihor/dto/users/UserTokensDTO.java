package com.uberTim12.ihor.dto.users;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserTokensDTO {
    private String accessToken;
    private String refreshToken;
}
