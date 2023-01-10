package com.uberTim12.ihor.dto.ride;

import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
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
public class FavoriteFullDTO {

    private Integer id;

    private String favoriteName;

    private Set<PathDTO> locations = new HashSet<>();

    private Set<UserRideDTO> passengers = new HashSet<>();

    private VehicleCategory vehicleType;
    private boolean babyTransport;

    private boolean petTransport;


    public FavoriteFullDTO(Favorite favorite){
        this(favorite.getId(), favorite.getFavoriteName(), favorite.getVehicleCategory(), favorite.isBabiesAllowed(), favorite.isPetsAllowed());

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

    public FavoriteFullDTO(Integer id, String favoriteName, VehicleCategory vehicleCategory, boolean babiesAllowed, boolean petsAllowed) {
        this.id = id;
        this.favoriteName = favoriteName;
        this.vehicleType = vehicleCategory;
        this.babyTransport = babiesAllowed;
        this.petTransport = petsAllowed;
    }
}
