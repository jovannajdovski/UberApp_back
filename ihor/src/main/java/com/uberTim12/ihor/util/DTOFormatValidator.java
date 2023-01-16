package com.uberTim12.ihor.util;

import com.uberTim12.ihor.dto.users.DriverDocumentDetailsDTO;
import com.uberTim12.ihor.dto.users.DriverRegistrationDTO;
import com.uberTim12.ihor.dto.users.UserRideDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.zip.DataFormatException;

public class DTOFormatValidator {


    public static void checkDriverRegistrationDTOValidity(DriverRegistrationDTO driverRegistrationDTO) throws DataFormatException {
        SimpleFormatValidator.checkBasicStringFieldValidity("name", driverRegistrationDTO.getName());
        SimpleFormatValidator.checkBasicStringFieldValidity("surname", driverRegistrationDTO.getSurname());
        SimpleFormatValidator.checkProfilePictureValidity(driverRegistrationDTO.getProfilePicture());
        SimpleFormatValidator.checkTelephoneNumberValidity(driverRegistrationDTO.getTelephoneNumber());
        SimpleFormatValidator.checkEmailValidity(driverRegistrationDTO.getEmail());
        SimpleFormatValidator.checkAddressValidity(driverRegistrationDTO.getAddress());
        SimpleFormatValidator.checkPasswordValidity(driverRegistrationDTO.getPassword());
    }


    public static void checkDriverDocumentDTOValidity(DriverDocumentDetailsDTO driverDocumentDTO) throws DataFormatException {
        SimpleFormatValidator.checkBasicStringFieldValidity("name", driverDocumentDTO.getName());
        SimpleFormatValidator.checkDocumentImageValidity(driverDocumentDTO.getDocumentImage());
    }
}
