package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.*;
import com.uberTim12.ihor.repository.users.IAdministratorRepository;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.repository.users.IUserRepository;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserRepository IUserRepository;
    @Autowired
    private IDriverRepository IDriverRepository;
    @Autowired
    private IPassengerRepository IPassengerRepository;

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



    @Override
    public boolean blockUser(Integer id) {
        Optional<User> user=IUserRepository.findById(id);
        if(user.isEmpty()) return false;
        else{
            user.get().setBlocked(true);
            IUserRepository.saveAndFlush(user.get());
            return true;
        }
    }

    @Override
    public boolean unblockUser(Integer id) {
        Optional<User> user=IUserRepository.findById(id);
        if(user.isEmpty()) return false;
        else{
            user.get().setBlocked(false);
            IUserRepository.saveAndFlush(user.get());
            return true;
        }
    }

    static class SetToPageConverter<T>{
        Page<T> convert(Set<T> set){
            return new PageImpl<>(set.stream().toList());
        }

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
