package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.PasswordResetToken;
import com.uberTim12.ihor.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    public Optional<PasswordResetToken> findByToken(String token);

    public void deleteAllByUser(User user);

}
