package com.alexander.sistema_cerro_verde_backend.service.seguridad;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class HashIdSerializer extends JsonSerializer<Integer> {

    @Override
    public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // Instanciamos el servicio (o podrías hacerlo estático)
            HashService hashService = new HashService();
            String encriptado = hashService.encrypt(value);
            gen.writeString(encriptado);
        }
    }
}