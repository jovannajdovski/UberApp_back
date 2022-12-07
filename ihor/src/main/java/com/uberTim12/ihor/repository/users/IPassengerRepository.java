package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface IPassengerRepository extends JpaRepository<Passenger, Integer> {

    public boolean existsByEmail(String Email);

    @Query("select p from Passenger p join fetch p.rides e where p.id =?1")
    public Passenger findByIdWithRides(Integer id);
}
