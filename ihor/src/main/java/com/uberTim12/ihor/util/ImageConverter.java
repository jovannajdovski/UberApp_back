package com.uberTim12.ihor.util;


import org.springframework.util.Base64Utils;

import java.util.Objects;

public class ImageConverter {

    public static String encodeToString(byte[] imageBytes) {
        if (imageBytes==null){
            return null;
        }
        String imageString = null;
        imageString = Base64Utils.encodeToString(imageBytes);

        return "data:image/jpeg;base64,"+imageString;
    }

    public static byte[] decodeToImage(String imageString) {

        if (Objects.equals(imageString, null)){
            return null;
        }
        String imageBase64;
        if (imageString.contains(",")){
            imageBase64 = imageString.split(",")[1];
        } else {
            imageBase64 = imageString;
        }
        return Base64Utils.decodeFromString(imageBase64);
    }
}
