package com.uberTim12.ihor.repository.ride;

import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFavoriteRepository extends JpaRepository<Favorite, Integer> {

    @Query("select f from Favorite f join f.passengers p join f.paths l where f.id =?1")
    Optional<Favorite> findById(Integer id);

    @Query("select f from Favorite f join f.passengers p join f.paths l")
    List<Favorite> getAllWithPassengersAndPaths();

    @Query("select f from Favorite f join f.passengers p where ?1 member of f.passengers")
    List<Favorite> findAllForPassenger(Passenger passenger, Pageable pageable);

    @Query("select f.passengers from Favorite as f join f.passengers as p where f.id =?1")
    List<Passenger> findPassengersForFavorite(Integer id);
}
