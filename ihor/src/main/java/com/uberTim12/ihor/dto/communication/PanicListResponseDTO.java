package com.uberTim12.ihor.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PanicListResponseDTO {

    private Integer totalCount;
    private List<PanicDTO> results;
}
