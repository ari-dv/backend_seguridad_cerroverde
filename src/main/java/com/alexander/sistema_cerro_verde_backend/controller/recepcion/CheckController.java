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

import com.alexander.sistema_cerro_verde_backend.entity.recepcion.CheckinCheckout;
import com.alexander.sistema_cerro_verde_backend.service.recepcion.CheckinCheckoutService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@CrossOrigin("*") 
@RestController
@RequestMapping("/cerro-verde/recepcion")
public class CheckController {

    @Autowired
    private CheckinCheckoutService checkService;

    @Autowired
    private HashService hashService;

    @GetMapping("/checks")
    public List<CheckinCheckout> buscarTodos() {
        return checkService.buscarTodos();
    }

    @PostMapping("/checks")
    public CheckinCheckout guardar(@RequestBody CheckinCheckout check) {   
        checkService.guardar(check);     
        return check;
    }

    @PutMapping("/checks/{hash}")
    public ResponseEntity<?> modificar(
            @PathVariable("hash") String hash,
            @RequestBody CheckinCheckout check) {
    
        try {
            Integer idReal = hashService.decrypt(hash);
            check.setId_check(idReal); 
            CheckinCheckout actualizada = checkService.modificar(check);
            return ResponseEntity.ok(actualizada);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/checks/{hash}")
    public ResponseEntity<CheckinCheckout> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return checkService.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/checks/eliminar/{hash}")
    public ResponseEntity<Void> eliminar(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            checkService.eliminar(idReal);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}