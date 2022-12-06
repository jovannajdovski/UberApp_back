package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDriverDocumentRepository extends JpaRepository<DriverDocument, Integer> {
}
