package com.uberTim12.ihor.repository.route;

import com.uberTim12.ihor.model.route.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILocationRepository extends JpaRepository<Location, Integer> {
}
