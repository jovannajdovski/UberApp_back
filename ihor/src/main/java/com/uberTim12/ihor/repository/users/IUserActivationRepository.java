package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.PasswordResetToken;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.users.UserActivation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserActivationRepository extends JpaRepository<UserActivation,Integer> {

    public Optional<UserActivation> findByToken(Integer token);
    public void deleteAllByUser(User user);
}
