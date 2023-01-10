package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Note;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.repository.users.INoteRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.INoteService;
import com.uberTim12.ihor.service.users.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NoteService extends JPAService<Note> implements INoteService {
    private final INoteRepository noteRepository;
    private final IUserService userService;

    @Autowired
    public NoteService(INoteRepository noteRepository, IUserService userService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    @Override
    protected JpaRepository<Note, Integer> getEntityRepository() {
        return null;
    }

    @Override
    public Note create(Integer userId, String message) throws EntityNotFoundException {
        User user = userService.get(userId);
        LocalDateTime currentTime = LocalDateTime.now();

        Note note = new Note(currentTime, message, user);
        return save(note);
    }

    @Override
    public Page<Note> getFor(Integer userId, Pageable page) throws EntityNotFoundException {
        return noteRepository.findByUserId(userId, page);
    }
}
