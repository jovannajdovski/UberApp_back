package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.DriverDocument;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;

import java.util.List;

public interface IDriverDocumentService extends IJPAService<DriverDocument> {
    List<DriverDocument> getDocumentsFor(Integer driverId);

    DriverDocument addDocumentTo(Integer driverId, DriverDocument driverDocument);
}
