package com.uberTim12.ihor.model.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocationDTO {

    private Integer id;

    private String address;

    private String latitude;

    private String longitude;

    public LocationDTO(Location location)
    {
        this(location.getId(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude());
    }
}
