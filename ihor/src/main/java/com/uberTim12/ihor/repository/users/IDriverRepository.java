package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IDriverRepository extends JpaRepository<Driver, Integer> {

}
