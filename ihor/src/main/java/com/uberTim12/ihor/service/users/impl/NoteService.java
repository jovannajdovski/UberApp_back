package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Note;
import com.uberTim12.ihor.repository.users.INoteRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class NoteService extends JPAService<Note> implements INoteService {
    private final INoteRepository noteRepository;

    @Autowired
    public NoteService(INoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    protected JpaRepository<Note, Integer> getEntityRepository() {
        return null;
    }
}
