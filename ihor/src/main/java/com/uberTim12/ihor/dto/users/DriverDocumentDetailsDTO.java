package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.DriverDocument;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DriverDocumentDetailsDTO {
    @NotEmpty
    private String name;
    @Pattern(regexp = "^(data:image/jpeg;base64,|data:image/png;base64,)")
    private String documentImage;

    public DriverDocumentDetailsDTO(DriverDocument driverDocument)
    {
        this(driverDocument.getName(),
                driverDocument.getPicture()
        );
    }
}
