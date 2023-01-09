package com.uberTim12.ihor.dto.communication;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NoteDTO {
    Integer id;
    LocalDateTime date;
    String message;
}
