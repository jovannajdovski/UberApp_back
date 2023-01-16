package com.uberTim12.ihor.dto.communication;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewRequestDTO {
    @DecimalMin(value = "0.0")
    @DecimalMax(value="5.0")
    private Double rating;
    @NotEmpty
    private String comment;
}
