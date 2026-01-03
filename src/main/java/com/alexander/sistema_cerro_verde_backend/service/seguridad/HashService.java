package com.alexander.sistema_cerro_verde_backend.service.seguridad; 
import org.hashids.Hashids;
import org.springframework.stereotype.Service;

@Service
public class HashService {
    
    // "MiSecreto..." es tu llave privada. ¡No la compartas!
    // 10 es la longitud mínima que tendrán los códigos (ej: aB3dE5xYz9)
    private final Hashids hashids = new Hashids("MiSecretoCerroVerde2025", 10);

    public String encrypt(Integer id) {
        if (id == null) return null;
        return hashids.encode(id);
    }

    public Integer decrypt(String hash) {
        long[] numbers = hashids.decode(hash);
        if (numbers.length > 0) {
            return (int) numbers[0];
        }
        throw new IllegalArgumentException("ID inválido");
    }
}