package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Passenger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PassengerIdentificatorsDTO {
    private Integer id;
    private String email;
    public PassengerIdentificatorsDTO(Passenger passenger)
    {
        this(passenger.getId(), passenger.getEmail());
    }
}
