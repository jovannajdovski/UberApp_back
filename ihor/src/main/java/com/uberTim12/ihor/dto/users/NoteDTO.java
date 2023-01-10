package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Note;
import com.uberTim12.ihor.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NoteDTO {

    private Integer id;
    private LocalDateTime date;
    private String message;
    private User user;

    public NoteDTO(Note note) {
        id = note.getId();
        date = note.getDate();
        message = note.getMessage();
        user = note.getUser();
    }
}
