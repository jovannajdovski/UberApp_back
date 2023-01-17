package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.exception.IncorrectCodeException;
import com.uberTim12.ihor.exception.UserActivationExpiredException;
import com.uberTim12.ihor.model.users.PasswordResetToken;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.repository.users.IUserActivationRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IUserActivationService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class UserActivationService extends JPAService<UserActivation> implements IUserActivationService {

    @Value("${angular.path}")
    private String angularPath;
    private final IUserActivationRepository userActivationRepository;
    private final IUserService userService;
    private final JavaMailSender mailSender;

    private static final String EMAIL_SENDER = "jnizvodno@gmail.com";

    @Autowired
    public UserActivationService(IUserActivationRepository userActivationRepository, IUserService userService, JavaMailSender mailSender) {
        this.userActivationRepository = userActivationRepository;
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @Override
    protected JpaRepository<UserActivation, Integer> getEntityRepository() {
        return userActivationRepository;
    }

    @Override
    public void create(User user) throws MessagingException, UnsupportedEncodingException {
        Integer activationToken = generateToken();
        UserActivation userActivation = new UserActivation(user, LocalDateTime.now(), LocalDateTime.now().plusYears(1), activationToken);

        userActivationRepository.deleteAllByUser(user);
        save(userActivation);
//        mailSender.send(constructResetTokenEmail(activationToken, user));
    }

    private Integer generateToken() {
        Random rnd = new Random();
        return 100000 + rnd.nextInt(900000);
    }
    private MimeMessage constructResetTokenEmail(Integer token, User user) throws MessagingException, UnsupportedEncodingException {
        String url = angularPath + "/account-activated?token=" + token;
        String content = "<p>Hello " + user.getName() + ",</p>"
                + "<p>You have requested to activate your account.</p>"
                + "<p>Click the link below to activate:</p>"
                + "<p><b><a href=\"" + url + "\">Activate account</a></b></p>"
                + "<p>Or use activation code: <b>" + token + "</b></p>";
        return constructEmail("Account activation", content, user);
    }

    private MimeMessage constructEmail(String subject, String body,
                                       User user) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(EMAIL_SENDER, "Ihor Support");
        helper.setTo(user.getEmail());

        helper.setSubject(subject);
        helper.setText(body, true);

        return message;
    }

    @Override
    public void activate(Integer activationId) throws EntityNotFoundException, UserActivationExpiredException{
        UserActivation userActivation = getByToken(activationId);

        if (userActivation == null) {
            throw new EntityNotFoundException("Activation with entered id does not exist!");
        }
        if (userActivation.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new UserActivationExpiredException("Activation expired. Register again!");

        userActivation.getUser().setActive(true);
        userActivationRepository.deleteAllByUser(userActivation.getUser());
        userService.save(userActivation.getUser());
    }

    private UserActivation getByToken(Integer token) {
        return userActivationRepository.findByToken(token).orElse(null);
    }
}
