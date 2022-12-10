package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IDriverRepository extends JpaRepository<Driver, Integer> {

    @Query("select d from Driver d where d.email = ?1")
    Driver findByEmail(String email);

    @Query("select d from Driver d join fetch d.documents where d.id =?1")
    Driver findOneWithDocuments(Integer driverId);

}
