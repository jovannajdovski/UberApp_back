package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.users.AdminRegistrationDTO;
import com.uberTim12.ihor.dto.users.UserDTO;
import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.service.users.impl.AdministratorService;
import com.uberTim12.ihor.service.users.interfaces.IAdministratorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "api/admin")
public class AdminController {

    private final IAdministratorService adminService;

    @Autowired
    public AdminController(AdministratorService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getAdmin(@PathVariable Integer id) {
        try {
            Administrator admin = adminService.get(id);
            return new ResponseEntity<>(new UserDTO(admin), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrator does not exist!");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @RequestBody AdminRegistrationDTO adminDTO) {
        try {
            Administrator admin = adminService.update(id, adminDTO.getName(), adminDTO.getSurname(), adminDTO.getProfilePicture(),
                    adminDTO.getTelephoneNumber(), adminDTO.getEmail(), adminDTO.getAddress(), adminDTO.getPassword());
            return new ResponseEntity<>(new UserDTO(admin), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrator does not exist!");
        }

    }
}
