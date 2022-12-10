package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.DriverDocument;

import java.util.List;

public interface IDriverDocumentService {

    DriverDocument save(DriverDocument driverDocument);
    void remove(Integer id);
    DriverDocument findOne(Integer id);
    List<DriverDocument> getDocumentsFor(Integer driverId);
}
