package com.uberTim12.ihor.dto.communication;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestNoteDTO {
    @NotEmpty
    @Length(max = 100)
    String message;
}
