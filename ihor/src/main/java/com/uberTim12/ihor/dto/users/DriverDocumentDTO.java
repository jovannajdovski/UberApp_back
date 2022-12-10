package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.DriverDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverDocumentDTO {
    private Integer id;
    private String name;
    private String documentImage;
    private Integer driverId;

    public DriverDocumentDTO(DriverDocument driverDocument)
    {
        this(driverDocument.getId(),
                driverDocument.getName(),
                driverDocument.getPicture(),
                driverDocument.getDriver().getId()
        );
    }

}
