package com.uberTim12.ihor.security;

import com.uberTim12.ihor.service.users.impl.UserService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthUtil {

    private final IUserService userService;

    @Autowired
    public AuthUtil(IUserService userService) {
        this.userService = userService;
    }

    public boolean hasId (Integer id)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return Objects.equals(userService.findByEmail(email).getId(), id);
    }

    public boolean hasRole (String roleName)
    {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
    }
}
