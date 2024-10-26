package com.fitsharingapp.application.common;

import java.util.Base64;

public class Base64Utils {

    public static String bytesToBase64(byte[] imageBytes) {
        if (imageBytes != null) {
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
        return null;
    }

    public static byte[] base64ToBytes(String base64String) {
        if (base64String != null) {
            return Base64.getDecoder().decode(base64String.split(",")[1]);
        }
        return null;
    }

}
