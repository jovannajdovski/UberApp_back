package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.users.AdminRegistrationDTO;
import com.uberTim12.ihor.dto.users.UserDTO;
import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.service.users.impl.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/admin")
public class AdminController {

    private final AdministratorService adminService;

    @Autowired
    public AdminController(AdministratorService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAdmin(@PathVariable Integer id) {

        Administrator admin = adminService.get(id);

        if (admin == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        } else {
            return new ResponseEntity<>(new UserDTO(admin), HttpStatus.OK);
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @RequestBody AdminRegistrationDTO adminDTO) {

        Administrator admin = adminService.get(id);

        if (admin == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        }

        admin.setName(adminDTO.getName());
        admin.setSurname(adminDTO.getSurname());
        admin.setProfilePicture(adminDTO.getProfilePicture());
        admin.setTelephoneNumber(adminDTO.getTelephoneNumber());
        admin.setEmail(adminDTO.getEmail());
        admin.setAddress(adminDTO.getAddress());

        if (!adminDTO.getPassword().equals("")){
            admin.setPassword(adminDTO.getPassword());
        }

        admin = adminService.save(admin);

        return new ResponseEntity<>(new UserDTO(admin), HttpStatus.OK);
    }
}
