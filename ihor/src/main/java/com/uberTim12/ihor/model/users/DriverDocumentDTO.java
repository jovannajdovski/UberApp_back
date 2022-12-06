package com.uberTim12.ihor.model.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverDocumentDTO {
    private Integer id;
    private String name;
    private String picture;
    private Driver driver;

    public DriverDocumentDTO(DriverDocument driverDocument)
    {
        this(driverDocument.getId(),
                driverDocument.getName(),
                driverDocument.getPicture(),
                driverDocument.getDriver()
        );
    }

}
