package com.alexander.sistema_cerro_verde_backend.service.seguridad;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Arrays;

@Service
public class HashService {

    // TU SECRETO: Nadie debe saber esto.
    private static final String MI_CLAVE_SECRETA = "CerroVerde_S3cur3_P4ssw0rd_2025!"; 

    // Preparamos la llave AES correcta (32 bytes) basada en tu texto
    private SecretKeySpec getSecretKey() {
        try {
            byte[] key = MI_CLAVE_SECRETA.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key); // Crea una llave perfecta de 256 bits
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Error generando llave", e);
        }
    }

    public String encrypt(Integer id) {
        if (id == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            
            // Encriptamos el número convertido a texto
            byte[] idBytes = String.valueOf(id).getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = cipher.doFinal(idBytes);
            
            // Convertimos a String seguro para URL (sin +, / o =)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    public Integer decrypt(String hash) {
        if (hash == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());

            byte[] decodedBytes = Base64.getUrlDecoder().decode(hash);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            
            String idStr = new String(decryptedBytes, StandardCharsets.UTF_8);
            return Integer.parseInt(idStr);
        } catch (Exception e) {
            // Si alguien manipula la URL, el sistema falla aquí.
            throw new IllegalArgumentException("URL corrupta o intento de hackeo");
        }
    }
}