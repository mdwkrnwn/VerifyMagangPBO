package com.mycompany.verifymagang.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Utilitas hashing password menggunakan SHA-256 dengan salt acak.
 *
 * <p>Format nilai yang disimpan di kolom {@code users.password}:</p>
 * <pre>sha256$&lt;saltHex&gt;$&lt;hashHex&gt;</pre>
 *
 * <p>Hash dihitung dari {@code SHA-256(saltBytes || passwordBytes)}.</p>
 */
public final class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final String PREFIX = "sha256";
    private static final String SEPARATOR = "$";
    private static final int SALT_BYTES = 16;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private PasswordUtil() {
        // kelas utilitas
    }

    /**
     * Membuat hash password baru dengan salt acak.
     *
     * @param plainPassword password asli
     * @return nilai siap simpan berformat {@code sha256$salt$hash}
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Password tidak boleh null.");
        }
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        String saltHex = HEX.formatHex(salt);
        String hashHex = digestHex(salt, plainPassword);
        return PREFIX + SEPARATOR + saltHex + SEPARATOR + hashHex;
    }

    /**
     * Memverifikasi password terhadap nilai tersimpan.
     *
     * @param plainPassword password yang diinput user
     * @param storedValue   nilai dari kolom {@code users.password}
     * @return {@code true} bila cocok
     */
    public static boolean verify(String plainPassword, String storedValue) {
        if (plainPassword == null || storedValue == null) {
            return false;
        }
        String[] parts = storedValue.split("\\" + SEPARATOR);
        if (parts.length != 3 || !PREFIX.equals(parts[0])) {
            return false;
        }

        byte[] salt;
        try {
            salt = HEX.parseHex(parts[1]);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        String expected = parts[2];
        String actual = digestHex(salt, plainPassword);
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Mengecek apakah sebuah nilai sudah berbentuk hash (bukan plain text).
     *
     * @param value nilai yang dicek
     * @return {@code true} bila berformat hash SHA-256 bersalt
     */
    public static boolean isHashed(String value) {
        if (value == null) {
            return false;
        }
        String[] parts = value.split("\\" + SEPARATOR);
        return parts.length == 3 && PREFIX.equals(parts[0]);
    }

    private static String digestHex(byte[] salt, String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] result = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return HEX.formatHex(result);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritma " + ALGORITHM + " tidak tersedia.", ex);
        }
    }
}
