package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.DriverDocument;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DriverDocumentDetailsDTO {
    private String name;
    private String documentImage;

    public DriverDocumentDetailsDTO(DriverDocument driverDocument)
    {
        this(driverDocument.getName(),
                driverDocument.getPicture()
        );
    }
}
