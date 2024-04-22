package com.uberTim12.ihor.service.ride.impl;

import com.uberTim12.ihor.dto.ride.CreateFavoriteDTO;
import com.uberTim12.ihor.dto.route.FavoriteRouteForPassengerDTO;
import com.uberTim12.ihor.dto.route.PathDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.exception.FavoriteRideExceedException;
import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.route.Path;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IFavoriteRepository;
import com.uberTim12.ihor.security.AuthUtil;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.ride.interfaces.IFavoriteService;
import com.uberTim12.ihor.service.route.interfaces.IPathService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FavoriteService extends JPAService<Favorite> implements IFavoriteService {
    private final IFavoriteRepository favoriteRepository;
    private final IPassengerService passengerService;
    private final IPathService pathService;

    @Autowired
    public FavoriteService(IFavoriteRepository favoriteRepository, IPassengerService passengerService, IPathService pathService) {
        this.favoriteRepository = favoriteRepository;
        this.passengerService = passengerService;
        this.pathService = pathService;
    }

    @Override
    protected JpaRepository<Favorite, Integer> getEntityRepository() {
        return favoriteRepository;
    }

    @Override
    public Favorite create(CreateFavoriteDTO favoriteDTO) throws FavoriteRideExceedException, EntityNotFoundException, EntityPropertyIsNullException {

        if (favoriteDTO.getFavoriteName()==null || favoriteDTO.getFavoriteName().equals("")){
            throw new EntityPropertyIsNullException("Favorite name cant be empty!");
        }
        if (favoriteDTO.getLocations().isEmpty()){
            throw new EntityPropertyIsNullException("Favorite path cant be empty!");
        }

        for (UserRideDTO user: favoriteDTO.getPassengers()){
            Passenger passenger = passengerService.get(user.getId());
            if (passenger.getFavoriteRoutes().size()>9){
                throw new FavoriteRideExceedException("Number of favorite rides cannot exceed 10!");
            }
        }

        Favorite favorite = new Favorite();
        favorite.setFavoriteName(favoriteDTO.getFavoriteName());
        favorite.setVehicleCategory(favoriteDTO.getVehicleType());
        favorite.setBabiesAllowed(favoriteDTO.isBabyTransport());
        favorite.setPetsAllowed(favoriteDTO.isPetTransport());

        Set<Path> paths = new HashSet<>();
        for (PathDTO pathDTO : favoriteDTO.getLocations()) {
            Path path = new Path();

            Location departure = pathDTO.getDeparture().generateLocation();
            Location destination = pathDTO.getDestination().generateLocation();

            path.setStartPoint(departure);
            path.setEndPoint(destination);

            path = pathService.save(path);
            paths.add(path);
        }
        favorite.setPaths(paths);

        Set<Passenger> passengers = new HashSet<>();
        for (UserRideDTO userDTO : favoriteDTO.getPassengers()) {
            Passenger passenger = passengerService.get(userDTO.getId());
            passengers.add(passenger);
        }
        favorite.setPassengers(passengers);

        favorite = save(favorite);

        return favorite;
    }

    @Override
    public List<Favorite> getForPassenger(Integer id) throws EntityNotFoundException{
        Passenger passenger = passengerService.findByIdWithFavorites(id);
        if (passenger == null) throw  new EntityNotFoundException("Passenger does not exist!");
        return new ArrayList<>(passenger.getFavoriteRoutes());
    }

    @Override
    public FavoriteRouteForPassengerDTO isFavoriteRouteForPassenger(String from, String to, Integer passengerId) throws EntityNotFoundException{
        Passenger passenger = passengerService.get(passengerId);

        List<Favorite> favoritesForPassenger = favoriteRepository.findAllForPassengers(passenger);

        FavoriteRouteForPassengerDTO favoriteRouteForPassengerDTO = new FavoriteRouteForPassengerDTO(false,0);
        if (favoritesForPassenger.isEmpty()){
            return favoriteRouteForPassengerDTO;
        }

        for (Favorite favorite: favoritesForPassenger){
            if (favorite.getPaths().iterator().next().getStartPoint().getAddress().equals(from) &&
                    favorite.getPaths().iterator().next().getEndPoint().getAddress().equals(to)){
                favoriteRouteForPassengerDTO.setFavorite(true);
                favoriteRouteForPassengerDTO.setFavoriteId(favorite.getId());
                return  favoriteRouteForPassengerDTO;
            }
        }
        return  favoriteRouteForPassengerDTO;
    }

    @Override
    public List<Favorite> getAll() {
        return favoriteRepository.getAllWithPassengersAndPaths();
    }
}
