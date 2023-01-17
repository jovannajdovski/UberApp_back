package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateFavoriteDTO {

    @NotEmpty
    private String favoriteName;

    @Valid
    @Size(min = 1,max = 1)
    private Set<PathDTO> locations = new HashSet<>();

    @Valid
    @Size(min = 1)
    private Set<UserRideDTO> passengers = new HashSet<>();

    private VehicleCategory vehicleType;
    @NotNull
    private boolean babyTransport;
    @NotNull
    private boolean petTransport;


    public CreateFavoriteDTO(Favorite favorite){
        this(favorite.getFavoriteName(), favorite.getVehicleCategory(), favorite.isBabiesAllowed(), favorite.isPetsAllowed());

        Set<UserRideDTO> passengers = new HashSet<>();
        for (User u : favorite.getPassengers()){
            passengers.add(new UserRideDTO(u));
        }
        this.passengers = passengers;

        Set<PathDTO> locations = new HashSet<>();
        for (Path p : favorite.getPaths()){
            locations.add(new PathDTO(p));
        }
        this.locations = locations;
    }

    public CreateFavoriteDTO(String favoriteName, VehicleCategory vehicleCategory, boolean babiesAllowed, boolean petsAllowed) {
        this.favoriteName = favoriteName;
        this.vehicleType = vehicleCategory;
        this.babyTransport = babiesAllowed;
        this.petTransport = petsAllowed;
    }
}
