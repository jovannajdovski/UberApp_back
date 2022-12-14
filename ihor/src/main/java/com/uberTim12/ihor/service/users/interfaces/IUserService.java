package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.dto.users.UserCredentialsDTO;
import com.uberTim12.ihor.dto.users.UserTokensDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {

    User findByEmail(String email);

    UserTokensDTO getUserTokens(UserCredentialsDTO userCredentialDTO);

    Page<User> getAll(Pageable page);

    boolean blockUser(Integer id);

    boolean unblockUser(Integer id);
}
