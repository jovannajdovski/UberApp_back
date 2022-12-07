package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.DriverDocument;

public interface IDriverDocumentService {

    DriverDocument save(DriverDocument driverDocument);
    void remove(Integer id);
}
