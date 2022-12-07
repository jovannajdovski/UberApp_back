package com.uberTim12.ihor.service.communication.interfaces;

import com.uberTim12.ihor.model.communication.Panic;

import java.util.List;

public interface IPanicService {
    Panic save(Panic panic);

    List<Panic> findAll();
}
