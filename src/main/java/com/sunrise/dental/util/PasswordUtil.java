package com.sunrise.dental.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** SHA-256 password hashing. Run main() once to generate the hash for your
 *  chosen admin password, then paste it into database/schema.sql. */
public class PasswordUtil {

    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainText.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean matches(String plainText, String hash) {
        return hash(plainText).equals(hash);
    }

    public static void main(String[] args) {
        String pwd = args.length > 0 ? args[0] : "Admin@123";
        System.out.println("Hash for \"" + pwd + "\": " + hash(pwd));
    }
}
