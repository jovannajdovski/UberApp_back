package com.uberTim12.ihor.model.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NoteDTO {
    Integer id;
    LocalDateTime creationTime;
    String message;
}
