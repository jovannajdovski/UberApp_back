package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.dto.users.UserCredentialsDTO;
import com.uberTim12.ihor.dto.users.UserTokensDTO;
import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.exception.PasswordDoesNotMatchException;
import com.uberTim12.ihor.exception.UserAlreadyBlockedException;
import com.uberTim12.ihor.exception.UserNotBlockedException;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.repository.users.IUserRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

@Service
public class UserService extends JPAService<User> implements IUserService, UserDetailsService {
    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    protected JpaRepository<User, Integer> getEntityRepository() {
        return userRepository;
    }

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
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void emailTaken(String email) throws EmailAlreadyExistsException {
        if (userRepository.findByEmail(email) != null)
            throw new EmailAlreadyExistsException("Email is already taken");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username);
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), Arrays.asList(user.getAuthority()));
    }

    @Override
    public void changePassword(Integer id, String oldPassword, String newPassword)
            throws EntityNotFoundException, PasswordDoesNotMatchException {
        User user = get(id);
        if (!user.getPassword().equals(bCryptPasswordEncoder.encode(oldPassword)))
            throw new PasswordDoesNotMatchException("Current password is not matching!");

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        save(user);
    }

    @Override
    public void blockUser(Integer id) throws EntityNotFoundException, UserAlreadyBlockedException {
        User user = get(id);
        if (user.isBlocked())
            throw new UserAlreadyBlockedException("User already blocked!");
        user.setBlocked(true);
        save(user);
    }

    @Override
    public void unblockUser(Integer id) throws EntityNotFoundException, UserNotBlockedException {
        User user = get(id);
        if (!user.isBlocked())
            throw new UserNotBlockedException("User is not blocked!");
        user.setBlocked(false);
        save(user);
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
