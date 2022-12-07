package com.uberTim12.ihor.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NoteDTO {
    Integer id;
    LocalDateTime date;
    String message;
}
