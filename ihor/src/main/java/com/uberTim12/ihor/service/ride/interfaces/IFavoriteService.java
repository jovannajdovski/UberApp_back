package com.uberTim12.ihor.service.ride.interfaces;

import com.uberTim12.ihor.dto.ride.CreateFavoriteDTO;
import com.uberTim12.ihor.dto.route.FavoriteRouteForPassengerDTO;
import com.uberTim12.ihor.exception.AccessDeniedException;
import com.uberTim12.ihor.exception.FavoriteRideExceedException;
import com.uberTim12.ihor.exception.UnauthorizedException;
import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;

import java.util.Set;

public interface IFavoriteService extends IJPAService<Favorite> {
    Favorite create(CreateFavoriteDTO favoriteDTO) throws FavoriteRideExceedException;

    Set<Favorite> getForPassenger() throws UnauthorizedException, AccessDeniedException;

    FavoriteRouteForPassengerDTO isFavoriteRouteForPassenger(String from, String to, Integer passengerId);
}
