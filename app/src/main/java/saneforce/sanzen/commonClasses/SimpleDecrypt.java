package saneforce.sanzen.commonClasses;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SimpleDecrypt {

    private static final String SALT = "some_salt_value";

    public static String decrypt(String strToDecrypt, String secretKey) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(strToDecrypt);

        byte[] iv = new byte[12];
        System.arraycopy(decoded, 0, iv, 0, iv.length);

        byte[] ciphertext = new byte[decoded.length - iv.length];
        System.arraycopy(decoded, iv.length, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey key = getKeyFromPassword(secretKey, SALT);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decrypted = cipher.doFinal(ciphertext);

        return new String(decrypted);
    }

    private static SecretKey getKeyFromPassword(String password, String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

}