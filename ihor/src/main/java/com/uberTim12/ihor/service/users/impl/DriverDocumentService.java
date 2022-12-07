package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.DriverDocument;
import com.uberTim12.ihor.repository.users.IDriverDocumentRepository;
import com.uberTim12.ihor.service.users.interfaces.IDriverDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverDocumentService implements IDriverDocumentService {

    private IDriverDocumentRepository driverDocumentRepository;
    @Autowired
    DriverDocumentService(IDriverDocumentRepository driverDocumentRepository) {
        this.driverDocumentRepository = driverDocumentRepository;
    }
    @Override
    public DriverDocument save(DriverDocument driverDocument) {
        return driverDocumentRepository.save(driverDocument);
    }
    @Override
    public void remove(Integer id) {
        driverDocumentRepository.deleteById(id);
    }
}
