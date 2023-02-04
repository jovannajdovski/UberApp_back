package com.uberTim12.ihor.service.ride.interfaces;

import com.uberTim12.ihor.dto.ride.CreateFavoriteDTO;
import com.uberTim12.ihor.exception.AccessDeniedException;
import com.uberTim12.ihor.exception.FavoriteRideExceedException;
import com.uberTim12.ihor.exception.UnauthorizedException;
import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;

import java.util.List;
import java.util.Set;

public interface IFavoriteService extends IJPAService<Favorite> {
    Favorite create(CreateFavoriteDTO favoriteDTO) throws FavoriteRideExceedException;

    List<Favorite> getForPassenger(Integer id);
}
