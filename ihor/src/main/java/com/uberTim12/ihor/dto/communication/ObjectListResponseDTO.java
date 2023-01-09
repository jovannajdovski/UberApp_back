package com.uberTim12.ihor.dto.communication;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ObjectListResponseDTO<T> {

    private Integer totalCount;
    private List<T> results;
}
