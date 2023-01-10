package com.uberTim12.ihor.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

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
