package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.exception.*;
import com.uberTim12.ihor.model.users.PasswordResetToken;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.repository.users.IPasswordResetTokenRepository;
import com.uberTim12.ihor.repository.users.IUserRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService extends JPAService<User> implements IUserService, UserDetailsService {

    @Value("${angular.path}")
    private String angularPath;
    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;
    private static final String EMAIL_SENDER = "jnizvodno@gmail.com";
    private final IUserRepository userRepository;

    private final IPasswordResetTokenRepository passwordResetTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JavaMailSender mailSender;

    @Autowired
    public UserService(IUserRepository userRepository, IPasswordResetTokenRepository passwordResetTokenRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.mailSender = mailSender;
    }

    @Override
    protected JpaRepository<User, Integer> getEntityRepository() {
        return userRepository;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void checkIntegrity(String email) throws AccessDeniedException {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new AccessDeniedException("Email is already taken");
        if (user.isBlocked() || !user.isActive())
            throw new AccessDeniedException("Email is already taken");
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

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword()))
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

    static class SetToPageConverter<T> {
        Page<T> convert(Set<T> set) {
            return new PageImpl<>(set.stream().toList());
        }
    }

    @Override
    public void forgotPassword(Integer userId) throws EntityNotFoundException, MessagingException, UnsupportedEncodingException {
        User user = get(userId);

        PasswordResetToken token = updatePasswordResetToken(user);
        mailSender.send(constructResetTokenEmail(token.getToken(), user));
    }

    private PasswordResetToken updatePasswordResetToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(generateToken());
        token.setTokenCreationDate(LocalDateTime.now());

        passwordResetTokenRepository.deleteAllByUser(user);
        return passwordResetTokenRepository.save(token);
    }

    private String generateToken() {
        StringBuilder token = new StringBuilder();

        return token.append(UUID.randomUUID().toString())
                .append(UUID.randomUUID().toString()).toString();
    }

    private MimeMessage constructResetTokenEmail(String token, User user) throws MessagingException, UnsupportedEncodingException {
        String url = angularPath + "/reset-password?token=" + token+"&id="+user.getId();
        String content = "<p>Hello " + user.getName() + ",</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><b><a href=\"" + url + "\">Change my password</a></b></p>"
                + "<p>Or use reset code: <b>" + token + "</b></p>";
        return constructEmail("Reset Password", content, user);
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
    public void resetPassword(Integer userId, String token, String password) throws IncorrectCodeException, CodeExpiredException, EntityNotFoundException {
        User userWithId = get(userId);

        PasswordResetToken passwordResetToken = getByTokenString(token);
        if (passwordResetToken == null) {
            throw new IncorrectCodeException("Code is expired or not correct!");
        }

        User user = passwordResetToken.getUser();
        if (user == null || user!=userWithId ) {
            throw new IncorrectCodeException("Code is expired or not correct!");
        }

        LocalDateTime tokenCreationDate = passwordResetToken.getTokenCreationDate();
        if (isTokenExpired(tokenCreationDate)) {
            throw new CodeExpiredException("Code is expired or not correct!");

        }

        updatePassword(user, password);
    }

    private PasswordResetToken getByTokenString(String token) {
        return passwordResetTokenRepository.findByToken(token).orElse(null);
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
    }

    private void updatePassword(User user, String newPassword) {
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        save(user);

        passwordResetTokenRepository.deleteAllByUser(user);
    }

}
