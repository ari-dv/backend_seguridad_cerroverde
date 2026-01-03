package com.alexander.sistema_cerro_verde_backend.controller.seguridad;

import java.util.List;
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

import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Roles;
import com.alexander.sistema_cerro_verde_backend.repository.seguridad.RolesRepository;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.IRolesService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin(origins = "*") 
public class RolesController {

    @Autowired
    private IRolesService rolesService;
    
    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private HashService hashService;

    @GetMapping("/roles/")
    public ResponseEntity<List<Roles>> obtenerTodosLosPermisos() {
        List<Roles> roles = rolesService.obtenerTodosLosRoles();
        return ResponseEntity.ok(roles); 
    }
    
    @PutMapping("/roles/{hash}")
    public ResponseEntity<?> actualizarRol(@PathVariable("hash") String hash, @RequestBody Roles rol) {
        try {
            Integer idReal = hashService.decrypt(hash);
            rol.setId(idReal);

            Optional<Roles> rolExistenteOpt = rolesRepository.findById(idReal);
            if (!rolExistenteOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Rol no encontrado");
            }

            Optional<Roles> rolConMismoNombre = rolesRepository.findByNombreRol(rol.getNombreRol());
            if (rolConMismoNombre.isPresent() && !rolConMismoNombre.get().getId().equals(idReal)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe otro rol con el nombre " + rol.getNombreRol());
            }

            Roles rolActualizado = rolesService.actualizarRol(rol);
            return ResponseEntity.ok(rolActualizado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el rol: " + e.getMessage());
        }
    }

    @GetMapping("/roles/{hash}")
    public ResponseEntity<Roles> obtenerRol(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            Roles rol = rolesService.obtenerRolPorId(idReal);
            
            if (rol == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(rol);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/roles/")
    public ResponseEntity<Roles> crearRol(@RequestBody Roles rol) throws Exception {
        return ResponseEntity.ok(rolesService.crearRol(rol));
    }

    @PostMapping("/roles-sp/")
    public ResponseEntity<Roles> crearRolSinPermisos(@RequestBody Roles rol) throws Exception {
        return ResponseEntity.ok(rolesService.crearRol(rol));
    }
    
    @DeleteMapping("/roles/eliminar/{hash}")
    public ResponseEntity<Void> eliminarRol(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            Roles existente = rolesService.obtenerRolPorId(idReal);
            
            if (existente == null) {
                return ResponseEntity.notFound().build();
            }
            rolesService.eliminarRol(idReal);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}