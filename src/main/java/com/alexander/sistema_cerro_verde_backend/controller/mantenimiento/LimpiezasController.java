package com.alexander.sistema_cerro_verde_backend.controller.mantenimiento;

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

import com.alexander.sistema_cerro_verde_backend.entity.mantenimiento.Limpiezas;
import com.alexander.sistema_cerro_verde_backend.service.mantenimiento.jpa.LimpiezasService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde/limpiezas")
@CrossOrigin("*")
public class LimpiezasController {
    
    @Autowired
    private LimpiezasService serviceLimpiezas;

    @Autowired
    private HashService hashService;
    
    @GetMapping("/ver") //Ver
    public List<Limpiezas> buscarTodos() {
        return serviceLimpiezas.buscarTodos();
    }

    @GetMapping("/limpiezas/{hash}") //Ver por Hash
    public ResponseEntity<Limpiezas> buscarPorId(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceLimpiezas.buscarPorId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrar") //Registrar
    public Limpiezas registrar(@RequestBody Limpiezas limpiezas) {
        serviceLimpiezas.registrar(limpiezas);
        return limpiezas;
    }

    @PutMapping("/actualizar/{hash}") //Actualizar
    public ResponseEntity<?> actualizar (@PathVariable("hash") String hash, @RequestBody Limpiezas limpiezas){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceLimpiezas.actualizar(idReal, limpiezas);
            return ResponseEntity.ok("Limpieza actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{hash}") //Eliminar
    public ResponseEntity<?> eliminarPorId (@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceLimpiezas.eliminarPorId(idReal);
            return ResponseEntity.ok("Limpieza eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}