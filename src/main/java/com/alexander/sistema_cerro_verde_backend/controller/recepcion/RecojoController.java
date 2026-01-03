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

import com.alexander.sistema_cerro_verde_backend.entity.recepcion.Recojo;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.RecojoService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class RecojoController {

    @Autowired
    private RecojoService recojoService;

    @Autowired
    private HashService hashService;

    @GetMapping("/recojos")
    public List<Recojo> buscarTodos() {
        return recojoService.buscarTodos();
    }

    @PostMapping("/recojos")
    public Recojo guardar(@RequestBody Recojo recojo) {   
        recojoService.guardar(recojo);     
        return recojo;
    }

    @PutMapping("/recojos/{hash}")
    public ResponseEntity<?> modificar(
            @PathVariable("hash") String hash,
            @RequestBody Recojo recojo) {
    
        try {
            Integer idReal = hashService.decrypt(hash);
            recojo.setId_recojo(idReal); 
            Recojo actualizada = recojoService.modificar(recojo);
            return ResponseEntity.ok(actualizada);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/recojos/{hash}")
    public ResponseEntity<Recojo> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return recojoService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/recojos/eliminar/{hash}")
    public ResponseEntity<Void> eliminar(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            recojoService.eliminar(idReal);
            return ResponseEntity.noContent().build(); 
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}