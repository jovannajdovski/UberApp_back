package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.model.users.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IAdministratorRepository extends JpaRepository<Administrator, Integer> {

}
