package com.alexander.sistema_cerro_verde_backend.controller.seguridad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexander.sistema_cerro_verde_backend.entity.AuditoriaHttp;
import com.alexander.sistema_cerro_verde_backend.repository.AuditoriaHttpRepository;
// AuditoriaController.java
// AuditoriaController.java
@RestController
@RequestMapping("/cerro-verde/auditoria")
@CrossOrigin("*")
public class AuditoriaController {
    
    @Autowired
    private AuditoriaHttpRepository auditoriaRepository;

    @GetMapping
    public List<AuditoriaHttp> listarAuditorias() {
        // SOLUCIÓN: Usar "fecha" porque así se llama en tu Entidad: private LocalDateTime fecha;
        return auditoriaRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha"));
    }
}