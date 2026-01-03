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

import com.alexander.sistema_cerro_verde_backend.entity.recepcion.Conductores;
import com.alexander.sistema_cerro_verde_backend.service.PlacaService;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.ConductoresService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class ConductoresController {

    @Autowired
    private ConductoresService conductorService;

    @Autowired
    private PlacaService placaService;

    @Autowired
    private HashService hashService;

    @GetMapping("/conductores")
    public List<Conductores> buscarTodos() {
        return conductorService.buscarTodos();
    }

    @PostMapping("/conductores")
    public Conductores guardar(@RequestBody Conductores conductor) {   
        conductorService.guardar(conductor);     
        return conductor;
    }

    @PutMapping("/conductores/{hash}")
    public ResponseEntity<?> modificar(
            @PathVariable("hash") String hash,
            @RequestBody Conductores conductor) {
    
        try {
            Integer idReal = hashService.decrypt(hash);
            conductor.setId_conductor(idReal); 
            Conductores actualizado = conductorService.modificar(conductor);
            return ResponseEntity.ok(actualizado);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/conductores/{hash}")
    public ResponseEntity<Conductores> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return conductorService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/conductores/eliminar/{hash}")
    public ResponseEntity<Void> eliminar(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            conductorService.eliminar(idReal);
            return ResponseEntity.noContent().build(); 
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/conductores/buscarplaca/{placa}")
    public ResponseEntity<String> buscarPorPlaca(@PathVariable String placa) {
        String resultado = placaService.consultarPlaca(placa);
        return ResponseEntity.ok(resultado);
    }
}