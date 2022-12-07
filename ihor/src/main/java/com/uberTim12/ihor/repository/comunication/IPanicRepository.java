package com.uberTim12.ihor.repository.communication;

import com.uberTim12.ihor.model.communication.Panic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPanicRepository extends JpaRepository<Panic, Integer> {
}
