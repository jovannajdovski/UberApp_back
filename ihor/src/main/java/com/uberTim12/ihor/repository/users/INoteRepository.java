package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INoteRepository extends JpaRepository<Note, Integer> {
}
