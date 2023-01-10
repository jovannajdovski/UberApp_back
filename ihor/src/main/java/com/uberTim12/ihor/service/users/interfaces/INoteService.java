package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.Note;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INoteService extends IJPAService<Note> {
    Note create(Integer userId, String message) throws EntityNotFoundException;

    Page<Note> getFor(Integer userId, Pageable page) throws EntityNotFoundException;
}
