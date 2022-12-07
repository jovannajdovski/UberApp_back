package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.UserActivation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserActivationRepository extends JpaRepository<UserActivation,Integer> {
}
