package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.dto.users.UserCredentialsDTO;
import com.uberTim12.ihor.dto.users.UserTokensDTO;
import com.uberTim12.ihor.model.users.*;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.repository.users.IUserRepository;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements IUserService, UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IPassengerRepository passengerRepository;

    @Override
    public UserTokensDTO getUserTokens(UserCredentialsDTO userCredentialDTO) {
        User user=userRepository.findByEmailAndPassword(userCredentialDTO.getEmail(), encryptPassword(userCredentialDTO.getPassword()));
        UserTokensDTO userTokensDTO=null;
        //if(user!=null)
            //TODO
            userTokensDTO=new UserTokensDTO("accessToken","refreshToken");
        return userTokensDTO;
    }

    @Override
    public Page<User> getAll(Pageable page) {
        return userRepository.findAll(page);
    }

    @Override
    public boolean blockUser(Integer id) {
        Optional<User> user=userRepository.findById(id);
        if(user.isEmpty()) return false;
        else{
            user.get().setBlocked(true);
            userRepository.saveAndFlush(user.get());
            return true;
        }
    }

    @Override
    public boolean unblockUser(Integer id) {
        Optional<User> user=userRepository.findById(id);
        if(user.isEmpty())
            return false;
        else{
            user.get().setBlocked(false);
            userRepository.saveAndFlush(user.get());
            return true;
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username);
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), Arrays.asList(user.getAuthority()));
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
