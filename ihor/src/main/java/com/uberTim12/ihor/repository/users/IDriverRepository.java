package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IDriverRepository extends JpaRepository<Driver, Integer> {

}
