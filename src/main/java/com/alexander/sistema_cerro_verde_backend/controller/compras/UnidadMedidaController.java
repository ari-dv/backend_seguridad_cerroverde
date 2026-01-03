package com.alexander.sistema_cerro_verde_backend.controller.compras;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexander.sistema_cerro_verde_backend.entity.compras.UnidadMedida;
import com.alexander.sistema_cerro_verde_backend.service.compras.jpa.UnidadMedidaService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*")
public class UnidadMedidaController {
    
    @Autowired
    private UnidadMedidaService serviceUnidad;

    @Autowired
    private HashService hashService;

    @GetMapping("/unidadmedida")
    public List<UnidadMedida> buscarTodos() {
        return serviceUnidad.buscarTodos(); //findAll
    }

    @PostMapping("/unidadmedida")
    public UnidadMedida guardar(@RequestBody UnidadMedida unidad) {
        serviceUnidad.guardar(unidad);
        return unidad;
    }

    @PutMapping("/unidadmedida/{hash}")
    public ResponseEntity<?> modificar(@PathVariable("hash") String hash, @RequestBody UnidadMedida unidad) {
        try {
            Integer idReal = hashService.decrypt(hash);
            // Asegúrate que el setter coincida con tu entidad (ej: setId_unidad o setId_unidad_medida)
            unidad.setIdUnidad(idReal); 
            
            serviceUnidad.modificar(unidad);
            return ResponseEntity.ok(unidad);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/unidadmedida/{hash}")
    public ResponseEntity<UnidadMedida> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceUnidad.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/unidadmedida/{hash}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceUnidad.eliminar(idReal);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Unidad de Medida eliminado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }
}