package com.uberTim12.ihor.dto.route;

import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PathDTO {

    private LocationDTO departure;

    private LocationDTO destination;

    public PathDTO(Path path){
        this(new LocationDTO(path.getStartPoint()), new LocationDTO(path.getEndPoint()));
    }
}
