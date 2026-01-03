package com.alexander.sistema_cerro_verde_backend.controller.recepcion;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.alexander.sistema_cerro_verde_backend.entity.recepcion.Reservas;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.ReservasService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class ReservaController {

    @Autowired
    private ReservasService reservaService;

    @Autowired
    private HashService hashService;

    @GetMapping("/reservas")
    public List<Reservas> buscarTodos() {
        return reservaService.buscarTodos();
    }

    @PostMapping("/reservas")
    public ResponseEntity<Reservas> guardar(@RequestBody Reservas reserva) {
        Reservas nuevaReserva = reservaService.guardar(reserva);
        return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
    }

    @PutMapping("/reservas/{hash}")
    public ResponseEntity<?> modificar(
        @PathVariable("hash") String hash,
        @RequestBody Reservas reserva) {
    
        try {
            Integer idReal = hashService.decrypt(hash);
            reserva.setId_reserva(idReal); 
            Reservas actualizada = reservaService.modificar(reserva);
            return ResponseEntity.ok(actualizada);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/reservas/{hash}")
    public ResponseEntity<Reservas> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return reservaService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/reservas/eliminar/{hash}")
    public ResponseEntity<String> eliminar(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            reservaService.eliminar(idReal);
            return ResponseEntity.ok("Reserva eliminada lógicamente y relaciones eliminadas.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar: " + e.getMessage());
        }
    }

    @PutMapping("/cancelar/{hash}")
    public ResponseEntity<?> cancelar(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            reservaService.cancelar(idReal); 

            return ResponseEntity.ok(Map.of(
                "mensaje", "Reserva cancelada correctamente",
                "reservaId", idReal
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al cancelar: " + e.getMessage()
            ));
        }
    }
}