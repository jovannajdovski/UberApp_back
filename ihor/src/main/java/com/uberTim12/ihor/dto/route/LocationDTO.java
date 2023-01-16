package com.uberTim12.ihor.dto.route;

import com.uberTim12.ihor.model.route.Location;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocationDTO {

    @NotEmpty
    private String address;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value="90.0")
    private Double latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value="180.0")
    private Double longitude;

    public LocationDTO(Location location)
    {
        this(location.getAddress(),
                location.getLatitude(),
                location.getLongitude());
    }

    public Location generateLocation(){
        Location location = new Location();
        location.setAddress(this.address);
        location.setLongitude(this.longitude);
        location.setLatitude(this.latitude);
        return location;
    }

}
