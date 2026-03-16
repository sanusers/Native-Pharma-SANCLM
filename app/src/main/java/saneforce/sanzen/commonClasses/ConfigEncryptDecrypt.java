package saneforce.sanzen.commonClasses;

import android.util.Base64;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import saneforce.sanzen.BuildConfig;


public class ConfigEncryptDecrypt {
    private static SecretKeySpec getKeySpec() {
        byte[] keyBytes = BuildConfig.AES_KEY.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static IvParameterSpec getIvSpec() {
        byte[] ivBytes = BuildConfig.AES_IV.getBytes(StandardCharsets.UTF_8);
        return new IvParameterSpec(ivBytes);
    }

    /*public static String decrypt(String cipherText) throws Exception {
        if (cipherText == null || cipherText.isEmpty()) return "";
        String cleanedText = cipherText.trim().replace("\"", "");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, getKeySpec(), getIvSpec());
        byte[] decodedBytes = Base64.decode(cleanedText, Base64.DEFAULT);
        byte[] original = cipher.doFinal(decodedBytes);
        return new String(original, StandardCharsets.UTF_8);
    }*/
    public static String decrypt(String cipherText) throws Exception {
        if (cipherText == null || cipherText.isEmpty())
            return "";
        String cleanedText = cipherText.trim().replace("\"", "");
        cleanedText = cleanedText.replace(" ", "+");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, getKeySpec(), getIvSpec());
        byte[] decodedBytes = Base64.decode(cleanedText, Base64.NO_WRAP);
        byte[] original = cipher.doFinal(decodedBytes);
        return new String(original, StandardCharsets.UTF_8);
    }

    public static String encrypt(String plainText) throws Exception {
        if (plainText == null) return "";

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getKeySpec(), getIvSpec());

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }
}