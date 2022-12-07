package com.uberTim12.ihor.model.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewRequestDTO {
    private Double rating;
    private String comment;
}
