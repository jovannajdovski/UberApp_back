package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.Authority;
import com.uberTim12.ihor.model.users.DriverDocument;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuthorityRepository extends JpaRepository<Authority, Integer> {
}
