package com.uberTim12.ihor.dto.route;

import com.uberTim12.ihor.model.route.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocationDTO {

    private String address;

    private Double latitude;

    private Double longitude;

    public LocationDTO(Location location)
    {
        this(location.getAddress(),
                location.getLatitude(),
                location.getLongitude());
    }
}
