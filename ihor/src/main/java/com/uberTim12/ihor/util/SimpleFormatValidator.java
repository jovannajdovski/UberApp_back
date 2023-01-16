package com.uberTim12.ihor.util;

import com.uberTim12.ihor.dto.ResponseMessageDTO;
import com.uberTim12.ihor.dto.users.DriverDetailsDTO;
import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.zip.DataFormatException;

public class SimpleFormatValidator {

    public static void checkProfilePictureValidity(String profilePicture) throws DataFormatException {
        if (!profilePicture.startsWith("data:image/jpeg;base64,") && !profilePicture.startsWith("data:image/png;base64,")){
            throw new DataFormatException("Field profilePicture format is not valid!");
        }

        String imageBase64 = profilePicture.split(",")[1];
        if (!imageBase64.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")){
            throw new DataFormatException("Field profilePicture format is not valid!");
        }

        if (ImageConverter.decodeToImage(profilePicture).length>1048576){
            throw new DataFormatException("Field profilePicture cannot be longer than 786432 characters (1mb)!");
        }
    }

    public static void checkBasicStringFieldValidity(String name, String field) throws DataFormatException {
        if (field==null){
            throw new DataFormatException(String.format("Field %s is required!",name));
        }
        if (field.trim().equals("")){
            throw new DataFormatException(String.format("Field %s is not valid!",name));
        }
    }

    public static void checkTelephoneNumberValidity(String telephoneNumber) throws DataFormatException {
        if (telephoneNumber==null){
            throw new DataFormatException("Field telephoneNumber is required!");
        }
        if (telephoneNumber.trim().equals("")){
            throw new DataFormatException("Field telephoneNumber format is not valid!");
        }
        if (!telephoneNumber.matches("[0-9]+[0-9 \\\\-]+")){
            throw new DataFormatException("Field telephoneNumber format is not valid!");
        }

    }

    public static void checkEmailValidity(String email) throws DataFormatException {
        if (email==null){
            throw new DataFormatException("Field email is required!");
        }
        if (email.trim().equals("")){
            throw new DataFormatException("Field email format is not valid!");
        }
        if (!email.matches(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$")){
            throw new DataFormatException("Field email format is not valid!");
        }
    }

    public static void checkPasswordValidity(String password) throws DataFormatException {
        if (password==null){
            throw new DataFormatException("Field password is required!");
        }
        if (password.trim().equals("")){
            throw new DataFormatException("Field password format is not valid!");
        }
        if (password.length()<6){
            throw new DataFormatException("Field password format is not valid!");
        }
    }

    public static void checkAddressValidity(String password) throws DataFormatException {
        if (password.trim().equals("")){
            throw new DataFormatException("Field address format is not valid!");
        }
    }

    public static void checkPageableValidity(Pageable pageable) throws DataFormatException {
        try {
            if (pageable.getPageSize()<1 || pageable.getPageNumber()<1) {
                throw new DataFormatException("Field page format is not valid!");
            }
        } catch (Exception e) {
            throw new DataFormatException("Field page format is not valid!");
        }
    }

    public static void checkIdValidity(Integer id) throws DataFormatException {
        if (id==null){
            throw new DataFormatException("Field id is required!");
        }
        if (id<1){
            throw new DataFormatException("Field id is not valid!");
        }
    }

    public static void checkDocumentImageValidity(String documentImage) throws DataFormatException {
        if (documentImage==null){
            throw new DataFormatException("Field documentImage is required!");
        }
        if (!documentImage.startsWith("data:image/jpeg;base64,") && !documentImage.startsWith("data:image/png;base64,")){
            throw new DataFormatException("Field documentImage format is not valid!");
        }

        String imageBase64 = documentImage.split(",")[1];
        if (!imageBase64.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")){
            throw new DataFormatException("Field documentImage format is not valid!");
        }

        if (ImageConverter.decodeToImage(documentImage).length>1048576){
            throw new DataFormatException("Field documentImage cannot be longer than 786432 characters (1mb)!");
        }
    }
}
