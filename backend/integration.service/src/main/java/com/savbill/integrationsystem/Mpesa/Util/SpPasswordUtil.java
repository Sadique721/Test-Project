package com.savbill.integrationsystem.Mpesa.Util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpPasswordUtil {

    public static String encrypt(Long id, String password, Long timestamp) {
        try {
            String input = id + password + timestamp;
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
