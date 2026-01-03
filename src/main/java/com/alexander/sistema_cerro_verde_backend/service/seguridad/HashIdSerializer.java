package com.alexander.sistema_cerro_verde_backend.service.seguridad; 

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hashids.Hashids; // O usa tu lógica de Base64 aquí

import java.io.IOException;

public class HashIdSerializer extends JsonSerializer<Integer> {

    @Override
    public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // IMPORTANTE: Usa la MISMA CLAVE SECRETA que en tu HashService
            Hashids hashids = new Hashids("MiSecretoCerroVerde2025", 10);
            
            // Encriptamos y escribimos el resultado como String en el JSON
            String idEncriptado = hashids.encode(value);
            gen.writeString(idEncriptado);
        }
    }
}