package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDriverDocumentRepository extends JpaRepository<DriverDocument, Integer> {

    @Query("select d from DriverDocument d where d.driver.id = ?1")
    List<DriverDocument> getDocumentsFor(Integer driverId);
}
