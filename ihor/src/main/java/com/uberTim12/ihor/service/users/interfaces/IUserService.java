package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.exception.PasswordDoesNotMatchException;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.dto.users.UserCredentialsDTO;
import com.uberTim12.ihor.dto.users.UserTokensDTO;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService extends IJPAService<User> {
    User findByEmail(String email);

    Page<User> getAll(Pageable page);

    void changePassword(Integer id, String oldPassword, String newPassword)
            throws EntityNotFoundException, PasswordDoesNotMatchException;

    void blockUser(Integer id);
    void unblockUser(Integer id);
    void emailTaken(String email) throws EmailAlreadyExistsException;
}
