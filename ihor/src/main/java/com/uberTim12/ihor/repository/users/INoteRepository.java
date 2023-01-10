package com.uberTim12.ihor.repository.users;

import com.uberTim12.ihor.model.users.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface INoteRepository extends JpaRepository<Note, Integer> {

    @Query("Select n from Note n where n.user.id = ?1")
    Page<Note> findByUserId(Integer id, Pageable page);
}
