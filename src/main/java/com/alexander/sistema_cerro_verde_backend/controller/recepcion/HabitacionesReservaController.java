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

import com.alexander.sistema_cerro_verde_backend.entity.recepcion.HabitacionesXReserva;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.jpa.HabitacionesReservaServiceImpl;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class HabitacionesReservaController {

    @Autowired
    private HabitacionesReservaServiceImpl habreservaService;

    @Autowired
    private HashService hashService;

    @GetMapping("/habitacionreservas")
    public List<HabitacionesXReserva> buscarTodos() {
        return habreservaService.buscarTodos();
    }

    @PostMapping("/habitacionreservas")
    public HabitacionesXReserva guardar(@RequestBody HabitacionesXReserva habreserva) {   
        habreservaService.guardar(habreserva);     
        return habreserva;
    }

    @PutMapping("/habitacionreservas/{hash}")
    public ResponseEntity<?> modificar(
            @PathVariable("hash") String hash,
            @RequestBody HabitacionesXReserva habreserva) {
    
        try {
            Integer idReal = hashService.decrypt(hash);
            habreserva.setId_hab_reserv(idReal);
            HabitacionesXReserva actualizada = habreservaService.modificar(habreserva);
            return ResponseEntity.ok(actualizada);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/habitacionreservas/{hash}")
    public ResponseEntity<HabitacionesXReserva> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return habreservaService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/habitacionreservas/eliminar/{hash}")
    public ResponseEntity<String> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            habreservaService.eliminar(idReal);
            return ResponseEntity.ok("Habitación relacionada a la reserva eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: ID inválido");
        }
    }
}