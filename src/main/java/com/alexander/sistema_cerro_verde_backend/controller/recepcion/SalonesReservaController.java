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

import com.alexander.sistema_cerro_verde_backend.entity.recepcion.SalonesXReserva;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.jpa.SalonesReservaServiceImpl;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class SalonesReservaController {

    @Autowired
    private SalonesReservaServiceImpl salreservaService;

    @Autowired
    private HashService hashService;

    @GetMapping("/salonreservas")
    public List<SalonesXReserva> buscarTodos() {
        return salreservaService.buscarTodos();
    }

    @PostMapping("/salonreservas")
    public SalonesXReserva guardar(@RequestBody SalonesXReserva salreserva) {   
        salreservaService.guardar(salreserva);     
        return salreserva;
    }

    @PutMapping("/salonreservas/{hash}")
    public ResponseEntity<?> modificar(
        @PathVariable("hash") String hash,
        @RequestBody SalonesXReserva salreserva) {
    
        try {
            Integer idReal = hashService.decrypt(hash);
            
            // Corregido: Antes tenías el getter, ahora usamos el setter para asignar el ID
            salreserva.setId_salon_reserv(idReal); 
            
            SalonesXReserva actualizada = salreservaService.modificar(salreserva);
            return ResponseEntity.ok(actualizada);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/salonreservas/{hash}")
    public ResponseEntity<SalonesXReserva> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return salreservaService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/salonreservas/eliminar/{hash}")
    public ResponseEntity<String> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            salreservaService.eliminar(idReal);
            return ResponseEntity.ok("Salón relacionado a la reserva eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: ID inválido");
        }
    }
}