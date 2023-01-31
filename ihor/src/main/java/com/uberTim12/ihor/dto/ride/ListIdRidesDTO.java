package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.model.route.Path;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ListIdRidesDTO {

    private Set<Integer> rides = new HashSet<>();
}
