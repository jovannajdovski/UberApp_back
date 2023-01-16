package com.uberTim12.ihor.dto.route;

import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PathDTO {

    @Valid
    @NotNull
    private LocationDTO departure;
    @Valid
    @NotNull
    private LocationDTO destination;

    public PathDTO(Path path){
        this(new LocationDTO(path.getStartPoint()), new LocationDTO(path.getEndPoint()));
    }
}
