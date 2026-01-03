package com.alexander.sistema_cerro_verde_backend.controller.recepcion;

import java.util.List;
import java.util.Optional;

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

import com.alexander.sistema_cerro_verde_backend.entity.recepcion.Salones;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.SalonesService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class SalonesController {

    @Autowired
    private SalonesService salonesService;

    @Autowired
    private HashService hashService;

    @GetMapping("/salones")
    public List<Salones> buscarTodos() {
        return salonesService.buscarTodos();
    }

    @PostMapping("/salones")
    public Salones guardar(@RequestBody Salones salon) {   
        salonesService.guardar(salon);     
        return salon;
    }

    @PutMapping("/salones/{hash}")
    public ResponseEntity<?> modificar(
            @PathVariable("hash") String hash,
            @RequestBody Salones salon) {
    
        try {
            Integer idReal = hashService.decrypt(hash);
            salon.setId_salon(idReal); 
            Salones actualizada = salonesService.modificar(salon);
            return ResponseEntity.ok(actualizada);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/salones/{hash}")
    public ResponseEntity<Salones> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return salonesService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/salones/eliminar/{hash}")
    public ResponseEntity<Void> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            salonesService.eliminar(idReal);
            return ResponseEntity.noContent().build(); 
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}