package com.alexander.sistema_cerro_verde_backend.controller.recepcion;

import java.util.List;

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

import com.alexander.sistema_cerro_verde_backend.entity.recepcion.Habitaciones;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.HabitacionesService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;


@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class HabitacionController {

    @Autowired
    private HabitacionesService habitacionesService;

    @Autowired
    private HashService hashService;

    @GetMapping("/habitaciones")
    public List<Habitaciones> buscarTodos() {
        return habitacionesService.buscarTodos();
    }

    @PostMapping("/habitaciones")
    public Habitaciones guardar(@RequestBody Habitaciones habitacion) {   
        habitacionesService.guardar(habitacion);     
        return habitacion;
    }

    
    @PutMapping("/habitaciones/{hash}")
    public ResponseEntity<?> modificar(
            @PathVariable("hash") String hash,
            @RequestBody Habitaciones habitacion) {
        try {
            Integer idReal = hashService.decrypt(hash); // Desencriptamos
            habitacion.setId_habitacion(idReal);
            
            Habitaciones actualizada = habitacionesService.modificar(habitacion);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/habitaciones/{hash}")
    public ResponseEntity<Habitaciones> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash); // Desencriptamos
            return habitacionesService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 5. ELIMINAR: CORREGIDO (Antes recibía Integer, ahora debe recibir String)
    @DeleteMapping("/habitaciones/eliminar/{hash}")
    public ResponseEntity<Void> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash); // Desencriptamos
            habitacionesService.eliminar(idReal);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    
}
