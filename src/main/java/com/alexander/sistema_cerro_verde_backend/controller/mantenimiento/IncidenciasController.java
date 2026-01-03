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

import com.alexander.sistema_cerro_verde_backend.entity.mantenimiento.Incidencias;
import com.alexander.sistema_cerro_verde_backend.service.SmsService;
import com.alexander.sistema_cerro_verde_backend.service.mantenimiento.jpa.IncidenciasService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde/incidencias")
@CrossOrigin("*")
public class IncidenciasController {
    
    @Autowired
    private IncidenciasService serviceIncidencias;

    @Autowired
    private SmsService mensajeService;
    
    @Autowired
    private HashService hashService;
    
    @GetMapping("/ver") //Ver
    public List<Incidencias> buscarTodos() {
        return serviceIncidencias.buscarTodos();
    }

    @GetMapping("/incidencias/{hash}") //Ver por Hash
    public ResponseEntity<Incidencias> buscarPorId(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceIncidencias.buscarPorId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrar") //Registrar
    public Incidencias registrar(@RequestBody Incidencias incidencias) {
        incidencias.setEstado_incidencia("pendiente");
        serviceIncidencias.registrar(incidencias);

        String mensaje = "Incidencia creada";     

        mensajeService.enviarSms(mensaje);
        return incidencias;
    }

    @PutMapping("/actualizar/{hash}") //Actualizar
    public ResponseEntity<?> actualizar (@PathVariable("hash") String hash, @RequestBody Incidencias incidencias){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceIncidencias.actualizar(idReal, incidencias);
            return ResponseEntity.ok("Incidencia actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{hash}") //Eliminar
    public ResponseEntity<?> eliminarPorId (@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceIncidencias.eliminarPorId(idReal);
            return ResponseEntity.ok("Incidencia eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}