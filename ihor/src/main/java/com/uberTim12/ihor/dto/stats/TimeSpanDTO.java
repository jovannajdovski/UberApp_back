package com.uberTim12.ihor.dto.stats;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TimeSpanDTO {
    @NotNull
    @Past
    public LocalDateTime from;
    @NotNull
    @Past
    public LocalDateTime to;
}
