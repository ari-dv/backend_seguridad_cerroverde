package com.alexander.sistema_cerro_verde_backend.controller.seguridad;

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

import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Permisos;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.IPermisosService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde/")
@CrossOrigin("*")
public class PermisosController {

    @Autowired
    private IPermisosService permisosService;

    @Autowired
    private HashService hashService;

    @GetMapping("/permisos/")
    public List<Permisos> obtenerTodosLosPermisos() {
        return permisosService.obtenerTodosLosPermisos();
    }

    @GetMapping("/permisos/{hash}")
    public ResponseEntity<Permisos> obtenerPermisoPorId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return ResponseEntity.ok(permisosService.obtenerPermiso(idReal));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/permisos/")
    public Permisos crearPermiso(@RequestBody Permisos permiso) {
        return permisosService.crearPermiso(permiso);
    }

    @PutMapping("/permisos/{hash}")
    public ResponseEntity<?> actualizarPermiso(@PathVariable("hash") String hash, @RequestBody Permisos permisos) {
        try {
            Integer idReal = hashService.decrypt(hash);
            permisos.setId(idReal); // Aseguramos que el ID sea el correcto
            
            Permisos actualizado = permisosService.editarPermiso(permisos);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }  

    @DeleteMapping("/permisos/{hash}")
    public ResponseEntity<?> eliminarPermiso(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            permisosService.eliminarPermiso(idReal);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}