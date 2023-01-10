package com.uberTim12.ihor.controller.users;

import com.uberTim12.ihor.dto.communication.*;
import com.uberTim12.ihor.dto.ride.RideFullDTO;
import com.uberTim12.ihor.dto.users.*;
import com.uberTim12.ihor.exception.PasswordDoesNotMatchException;
import com.uberTim12.ihor.exception.UserAlreadyBlockedException;
import com.uberTim12.ihor.exception.UserNotBlockedException;
import com.uberTim12.ihor.model.communication.Message;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.security.JwtUtil;
import com.uberTim12.ihor.service.communication.impl.MessageService;
import com.uberTim12.ihor.service.communication.impl.ReviewService;
import com.uberTim12.ihor.service.communication.interfaces.IMessageService;
import com.uberTim12.ihor.service.communication.interfaces.IReviewService;
import com.uberTim12.ihor.service.ride.impl.RideService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import com.uberTim12.ihor.service.users.impl.UserService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/user")
public class UserController {
    private final IRideService rideService;
    private final IUserService userService;
    private final IMessageService messageService;
    private final IReviewService reviewService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    UserController(RideService rideService,
                   UserService userService,
                   MessageService messageService,
                   ReviewService reviewService,
                   AuthenticationManager authenticationManager,
                   JwtUtil jwtUtil) {
        this.rideService = rideService;
        this.userService = userService;
        this.messageService = messageService;
        this.reviewService = reviewService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(value = "/{id}/ride",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserRides(@PathVariable Integer id,
                                          Pageable page,
                                          @RequestParam(required = false) String from,
                                          @RequestParam(required = false) String to
                                          )
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //TODO resiti
        LocalDateTime start = LocalDate.parse(from, formatter).atStartOfDay();
        LocalDateTime end = LocalDate.parse(to, formatter).atStartOfDay();
        Page<Ride> rides = rideService.getRides(id,start,end,page);
        List<RideFullDTO> ridesDTO=new ArrayList<>();
        if(rides==null) //TODO sve greske
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        else {
            for (Ride r : rides)
                ridesDTO.add(new RideFullDTO(r));

            ObjectListResponseDTO<RideFullDTO> res = new ObjectListResponseDTO<>(ridesDTO.size(),ridesDTO);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUsers(Pageable page)
    {
        Page<User> users = userService.getAll(page);
        List<UserDTO> usersDTO=new ArrayList<>();
        if(users==null) //TODO sve greske
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        else {
            for (User u : users)
                usersDTO.add(new UserDTO(u));
            ObjectListResponseDTO<UserDTO> res = new ObjectListResponseDTO<>(usersDTO.size(),usersDTO);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthTokenDTO> loginUser(@RequestBody UserCredentialsDTO userCredentialDTO)
    {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userCredentialDTO.getEmail(), userCredentialDTO.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(authentication);
            AuthTokenDTO tokenDTO = new AuthTokenDTO(token, token);
            return new ResponseEntity<>(tokenDTO, HttpStatus.OK);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong username or password!");
        }
    }

    @GetMapping(value = "/{id}/message",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObjectListResponseDTO<MessageDTO>> getUserMessages(@PathVariable Integer id)
    {
        try {
            userService.get(id);
            List<MessageDTO> messages = messageService.getMessages(id);
            ObjectListResponseDTO<MessageDTO> res = new ObjectListResponseDTO<>(messageService.getAll().size(),messages);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        }
    }

    @PostMapping(value = "/{id}/message",consumes=MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable("id") Integer senderId, @RequestBody SendingMessageDTO sendingMessageDTO)
    {
        try {
            Message message = messageService.sendMessage(senderId, sendingMessageDTO.getReceiverId(),
                    sendingMessageDTO.getRideId(), sendingMessageDTO.getMessage(), sendingMessageDTO.getType());
            return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable("id") Integer id)
    {
        try {
            userService.blockUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User is successfully blocked");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        } catch (UserAlreadyBlockedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already blocked!");
        }
    }

    @PutMapping(value = "/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable("id") Integer id)
    {
        try {
            userService.unblockUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User is successfully unblocked");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        } catch (UserNotBlockedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not blocked!");
        }    }

    @PostMapping(value = "/{id}/note",consumes=MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNote(@PathVariable Integer id, @RequestBody RequestNoteDTO requestNoteDTO)
    {
        if(id==null) //TODO sve greske
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        else {
            NoteDTO noteDTO = reviewService.createNote(id, requestNoteDTO);
            if(noteDTO==null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>(noteDTO, HttpStatus.OK);
        }
    }
    @GetMapping(value = "/{id}/note",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getNotes(@PathVariable Integer id, Pageable page)
    {
        if(id==null) //TODO sve greske
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong format of some field");
        else {
            Page<NoteDTO> notes = reviewService.getNotes(id, page);
            if(notes==null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else{
                List<NoteDTO> noteDTOS = new ArrayList<>();
                for (NoteDTO n : notes){
                    noteDTOS.add(n);
                }
                ObjectListResponseDTO<NoteDTO> res = new ObjectListResponseDTO<>(noteDTOS.size(),noteDTOS);
                return new ResponseEntity<>(res, HttpStatus.OK);}
        }
    }

    @PutMapping(value="/{id}/changePassword", consumes = "application/json")
    public ResponseEntity<String> changePassword(@PathVariable Integer id, @RequestBody NewPasswordDTO newPasswordDTO)
    {
        try {
            userService.changePassword(id, newPasswordDTO.getNew_password(), newPasswordDTO.getNew_password());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Password successfully changed!");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        } catch (PasswordDoesNotMatchException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is not matching!");
        }
    }
    @GetMapping(value="/{id}/resetPassword")
    public ResponseEntity<?> sendResetCodeToEmail(@PathVariable Integer id)
    {
        User user = userService.get(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist!");
        }
        //TODO

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Email with reset code has been sent!");
    }
    @PutMapping(value="/{id}/resetPassword", consumes = "application/json")
    public ResponseEntity<?> changePasswordWithResetCode(@PathVariable Integer id, @RequestBody ResetPasswordDTO resetPasswordDTO)
    {
        User user = userService.get(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist!");
        }

        if (!user.getPassword().equals(resetPasswordDTO.getCode())) // || is expired TODO
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code is expired or not correct!");
        }

        if (!resetPasswordDTO.getNew_password().equals("")){
            user.setPassword(resetPasswordDTO.getNew_password());
        }

        userService.save(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Password successfully changed!");
    }
}
