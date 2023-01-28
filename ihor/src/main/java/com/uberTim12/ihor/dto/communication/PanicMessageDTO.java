package com.uberTim12.ihor.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PanicMessageDTO {
    private String message;
    private Integer fromId;
}
