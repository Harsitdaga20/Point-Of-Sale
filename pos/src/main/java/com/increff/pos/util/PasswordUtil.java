package com.increff.pos.util;

import com.increff.pos.service.ApiException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtil {
    private static final int iterations=1000;
    private static final int keylength = 256;

    public static boolean validatePassword(String password,String stored) throws NoSuchAlgorithmException, ApiException {
        String[] parts = stored.split("\\|");
        String encodedSalt = parts[0];
        String encodedKey = parts[1];
        byte[] salt = Base64.getDecoder().decode(encodedSalt);
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);

        byte[] hashPassword=hash(password,salt);
        int diff=keyBytes.length^hashPassword.length;
        for(int i=0;i<hashPassword.length && i<keyBytes.length;i++){
            diff |=hashPassword[i]^keyBytes[i];
        }
        return diff==0;
    }

    public static String generateSecurePassword(String password) throws NoSuchAlgorithmException, ApiException {
        String finalval = null;
        byte[] salt=getSalt();
        byte[] securePassword = hash(password,salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        finalval = Base64.getEncoder().encodeToString(securePassword);

        return encodedSalt + "|" + finalval;
    }

    private static byte[] hash(String password,byte[] salt) throws NoSuchAlgorithmException, ApiException {


        char[] chars=password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, keylength);
        try
        {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            throw new ApiException("Error while hashing a password: " + e.getMessage());
        }
        finally
        {
            spec.clearPassword();
        }
    }
    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom=SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[32];
        secureRandom.nextBytes(salt);
        return salt;
    }

}
