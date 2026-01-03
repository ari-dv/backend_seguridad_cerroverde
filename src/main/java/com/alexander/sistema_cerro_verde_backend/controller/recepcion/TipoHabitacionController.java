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

import com.alexander.sistema_cerro_verde_backend.entity.recepcion.TipoHabitacion;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.TipoHabitacionService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class TipoHabitacionController {

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    @Autowired
    private HashService hashService;

    @GetMapping("/habitaciones/tipo")
    public List<TipoHabitacion> buscarTodos() {
        return tipoHabitacionService.buscarTodos();
    }

    @PostMapping("/habitaciones/tipo")
    public TipoHabitacion guardar(@RequestBody TipoHabitacion tipo) {   
        tipoHabitacionService.guardar(tipo);     
        return tipo;
    }
    
    @PutMapping("/habitaciones/tipo/{hash}")
    public ResponseEntity<?> modificar(
        @PathVariable("hash") String hash,
        @RequestBody TipoHabitacion tipo) {
    
        try {
            Integer idReal = hashService.decrypt(hash);
            tipo.setId_tipo_habitacion(idReal); 
            TipoHabitacion actualizada = tipoHabitacionService.modificar(tipo);
            return ResponseEntity.ok(actualizada);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/habitaciones/tipo/{hash}")
    public ResponseEntity<TipoHabitacion> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return tipoHabitacionService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/habitaciones/tipo/{hash}")
    public ResponseEntity<String> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            tipoHabitacionService.eliminar(idReal);
            return ResponseEntity.ok("Tipo de habitacion eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: ID inválido");
        }
    }
}