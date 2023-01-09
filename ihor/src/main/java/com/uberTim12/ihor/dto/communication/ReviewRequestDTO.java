package com.uberTim12.ihor.dto.communication;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewRequestDTO {
    private Double rating;
    private String comment;
}
