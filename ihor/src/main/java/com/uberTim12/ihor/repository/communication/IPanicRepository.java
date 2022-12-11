package com.uberTim12.ihor.repository.communication;

import com.uberTim12.ihor.model.communication.Panic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IPanicRepository extends JpaRepository<Panic, Integer> {

    @Query("select p from Panic as p join fetch p.currentRide as r join fetch r.passengers")
    List<Panic> findAllWithFetch();
}
