package com.uberTim12.ihor.repository.ride;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Administrator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IRideRepository extends JpaRepository<Ride, Integer> {
    // @Query("select r from Ride r where r.driver.id=?1 or r.passengers.")
    //TODO pretraziti u tabeli poveznici za putnike, za vozace u tabeli voznji???
    public Page<Ride> findById(Integer id, Pageable pageable);

}
