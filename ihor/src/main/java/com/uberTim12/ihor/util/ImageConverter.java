package com.uberTim12.ihor.util;


import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ImageConverter {

    public static String encodeToString(byte[] imageBytes) {
        if (imageBytes==null){
            return null;
        }
        byte[] bytesImageBase64 = Arrays.copyOfRange(imageBytes, 1, imageBytes.length);

        String imageString = null;
        String formatType;
        imageString = Base64Utils.encodeToString(bytesImageBase64);
        if ((int)imageBytes[0]==0){
            formatType = "png";
        } else {
            formatType = "jpeg";
        }
        return "data:image/"+formatType+";base64,"+imageString;
    }

    public static byte[] decodeToImage(String imageString) {

        if (Objects.equals(imageString, null)){
            return null;
        }
        String imageBase64;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (imageString.contains(",")){
            imageBase64 = imageString.split(",")[1];
            String formatType = imageString.split(",")[0].split("/")[1].split(";")[0];
            System.out.println(formatType);
            if(formatType.equals("png")){
                bytes.write(0);
            } else {
                bytes.write(1);
            }
        } else {
            imageBase64 = imageString;
        }
        try {
            bytes.write(Base64Utils.decodeFromString(imageBase64));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes.toByteArray();
    }
}
