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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexander.sistema_cerro_verde_backend.entity.recepcion.Huespedes;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.HuespedesService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class HuespedController {

    @Autowired
    private HuespedesService huespedService;

    @Autowired
    private HashService hashService;

    @GetMapping("/huespedes")
    public List<Huespedes> buscarTodos() {
        return huespedService.buscarTodos();
    }

    @PostMapping("/huespedes")
    public Huespedes guardar(@RequestBody Huespedes huesped) {   
        huespedService.guardar(huesped);     
        return huesped;
    }

    @GetMapping("/huespedes/{hash}")
    public ResponseEntity<Huespedes> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return huespedService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/huespedes/eliminar/{hash}")
    public ResponseEntity<Void> eliminar(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            huespedService.eliminar(idReal);
            return ResponseEntity.noContent().build(); 
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}