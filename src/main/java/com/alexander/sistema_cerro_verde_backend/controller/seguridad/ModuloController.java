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

import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Modulos;
import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Permisos;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.IModulosService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*") 
public class ModuloController {

    @Autowired
    private IModulosService moduloService;

    @Autowired
    private HashService hashService;

    @PostMapping("/modulos/")
    public Modulos guardarModulo(@RequestBody Modulos modulo) throws Exception{
        return moduloService.crearModulo(modulo);
    }

    @PutMapping("/modulos/{hash}")
    public ResponseEntity<?> actualizarModulo(@PathVariable("hash") String hash, @RequestBody Modulos modulo) {
        try {
            Integer idReal = hashService.decrypt(hash);
            modulo.setIdModulo(idReal); 
            Modulos actualizado = moduloService.editarModulo(modulo);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }  

    @GetMapping("/modulos/")
    public List<Modulos> listarModulos(){
        return moduloService.obtenerTodosLooModulos();
    }
    
    @GetMapping("/modulos/{hash}")
    public ResponseEntity<Modulos> obtenerModulo(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return ResponseEntity.ok(moduloService.obtenerModuloId(idReal));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/modulos/{hash}")
    public ResponseEntity<?> eliminarModulo(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            moduloService.eliminarModulo(idReal);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // ESTE ES EL QUE TE DABA EL ERROR 500
    @GetMapping("/modulos/{hash}/permisos")
    public ResponseEntity<List<Permisos>> obtenerPermisosPorModulo(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            List<Permisos> permisos = moduloService.obtenerPermisosPorModulo(idReal);
            return ResponseEntity.ok(permisos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}