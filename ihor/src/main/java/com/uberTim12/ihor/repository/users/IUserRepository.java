package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User,Integer> {
    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    Page<User> findAll(Pageable pageable);
}
