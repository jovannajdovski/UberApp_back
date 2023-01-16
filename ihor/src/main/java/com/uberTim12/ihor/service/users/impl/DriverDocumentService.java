package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.DriverDocument;
import com.uberTim12.ihor.repository.users.IDriverDocumentRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IDriverDocumentService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DriverDocumentService extends JPAService<DriverDocument> implements IDriverDocumentService {

    private final IDriverDocumentRepository driverDocumentRepository;

    private final IDriverService driverService;

    @Autowired
    DriverDocumentService(IDriverDocumentRepository driverDocumentRepository, IDriverService driverService) {
        this.driverDocumentRepository = driverDocumentRepository;
        this.driverService = driverService;
    }

    @Override
    protected JpaRepository<DriverDocument, Integer> getEntityRepository() {
        return driverDocumentRepository;
    }

    @Override
    public List<DriverDocument> getDocumentsFor(Integer driverId) throws EntityNotFoundException {
        Driver driver = driverService.get(driverId);
        return new ArrayList<>(driver.getDocuments());
    }

    @Override
    public DriverDocument addDocumentTo(Integer driverId, DriverDocument driverDocument) throws EntityNotFoundException {
        Driver driver = driverService.get(driverId);
        driverDocument.setDriver(driver);
        return save(driverDocument);
    }

}
