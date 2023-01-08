package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.exception.UserActivationExpiredException;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.repository.users.IUserActivationRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IUserActivationService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserActivationService extends JPAService<UserActivation> implements IUserActivationService {
    private final IUserActivationRepository userActivationRepository;
    private final IUserService userService;

    @Autowired
    public UserActivationService(IUserActivationRepository userActivationRepository, IUserService userService) {
        this.userActivationRepository = userActivationRepository;
        this.userService = userService;
    }

    @Override
    protected JpaRepository<UserActivation, Integer> getEntityRepository() {
        return userActivationRepository;
    }

    @Override
    public UserActivation create(User user) {
        UserActivation userActivation = new UserActivation(user, LocalDateTime.now(), LocalDateTime.now().plusYears(1));
        return save(userActivation);
    }

    @Override
    public void activate(Integer activationId) throws EntityNotFoundException, UserActivationExpiredException {
        UserActivation userActivation = get(activationId);
        if (userActivation.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new UserActivationExpiredException("Activation expired. Register again!");

        userActivation.getUser().setActive(true);
        userService.save(userActivation.getUser());
    }
}
