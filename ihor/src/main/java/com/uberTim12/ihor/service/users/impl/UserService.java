package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.users.UserCredentialsDTO;
import com.uberTim12.ihor.model.users.UserTokensDTO;
import com.uberTim12.ihor.repository.users.IAdministratorRepository;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.repository.users.IUserRepository;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserRepository IUserRepository;

    @Override
    public UserTokensDTO getUserTokens(UserCredentialsDTO userCredentialDTO) {
        User user=IUserRepository.findByEmailAndPassword(userCredentialDTO.getEmail(), encryptPassword(userCredentialDTO.getPassword()));
        UserTokensDTO userTokensDTO=null;
        if(user!=null)
            //TODO
            userTokensDTO=new UserTokensDTO("accessToken","refreshToken");
        return userTokensDTO;
    }

    @Override
    public Page<User> getAll(Pageable page) {
        return IUserRepository.findAll(page);
    }

    public static String encryptPassword(String password)
    {
        return password;
    }
    public static String decryptPassword(String hashCode)
    {
        return hashCode;
    }
}
