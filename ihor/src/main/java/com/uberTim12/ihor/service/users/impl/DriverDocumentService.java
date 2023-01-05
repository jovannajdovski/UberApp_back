package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.DriverDocument;
import com.uberTim12.ihor.repository.users.IDriverDocumentRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IDriverDocumentService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverDocumentService extends JPAService<DriverDocument> implements IDriverDocumentService {

    private IDriverDocumentRepository driverDocumentRepository;
    @Autowired
    DriverDocumentService(IDriverDocumentRepository driverDocumentRepository) {
        this.driverDocumentRepository = driverDocumentRepository;
    }

    @Override
    protected JpaRepository<DriverDocument, Integer> getEntityRepository() {
        return driverDocumentRepository;
    }
    @Override
    public List<DriverDocument> getDocumentsFor(Integer driverId) {
        return driverDocumentRepository.getDocumentsFor(driverId);
    }
}
