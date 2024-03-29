package com.uberTim12.ihor.dto.users;

import com.uberTim12.ihor.model.users.Passenger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerIdentificatorsDTO {
    private Integer id;
    private String email;
    public PassengerIdentificatorsDTO(Passenger passenger)
    {
        this(passenger.getId(), passenger.getEmail());
    }
}
