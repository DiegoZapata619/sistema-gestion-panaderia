package org.panaderia.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encriptador {
    public static String sha256(String mensaje) {
    try {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte [] digest = sha.digest(mensaje.getBytes());
        StringBuilder hexString = new StringBuilder();
        //formato hexadecimal
        for (byte b : digest){
            hexString.append(String.format("%02x",b));
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e){
        throw new RuntimeException(e);
    }
}
}
