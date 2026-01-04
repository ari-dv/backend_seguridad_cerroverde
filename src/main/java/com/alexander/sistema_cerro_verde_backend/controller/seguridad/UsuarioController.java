package com.alexander.sistema_cerro_verde_backend.controller.seguridad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Roles;
import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Usuarios;
import com.alexander.sistema_cerro_verde_backend.excepciones.CorreoYaRegistradoException;
import com.alexander.sistema_cerro_verde_backend.excepciones.UsuarioYaRegistradoException;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.IUsuariosService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.jpa.UsuariosService;

@RestController
@RequestMapping("/cerro-verde/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private IUsuariosService usuarioService;

    @Autowired
    private UsuariosService usuarioServiceImpl;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private HashService hashService;

    // 1. LISTAR TODOS (No requiere hash)
    @GetMapping("/")
    public List<Usuarios> listarUsuarios(){
        return usuarioServiceImpl.obtenerTodosUsuarios();
    }

    // 2. GUARDAR (Usa la lógica detallada de excepciones del código 1)
    @PostMapping("/")
    public ResponseEntity<?> guardUsuario(@RequestBody Usuarios usuario) {
        try {
            usuario.setPassword(this.bCryptPasswordEncoder.encode(usuario.getPassword()));
            usuario.setIdUsuario(null);

            // Se asigna Rol por defecto si viene nulo (Usé ID 2 basado en el primer código, cámbialo a 1 si prefieres)
            if (usuario.getRol() == null) {
                Roles rolPorDefecto = new Roles();
                rolPorDefecto.setId(2); 
                usuario.setRol(rolPorDefecto);
            }

            Usuarios usuarioGuardado = usuarioService.guardarUsuario(usuario);
            return ResponseEntity.ok(usuarioGuardado);

        } catch (CorreoYaRegistradoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está registrado.");
        } catch (UsuarioYaRegistradoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con esos nombres y apellidos.");
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("DNI")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el usuario.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el usuario.");
        }
    }

    // 3. OBTENER POR ID (Usa HashService del código 2)
    @GetMapping("/{hash}")
    public ResponseEntity<Usuarios> obtenerUsuarioPorId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            Usuarios usuario = usuarioServiceImpl.obtenerUsuarioPorId(idReal);
            return (usuario != null) ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 4. OBTENER POR USERNAME
    @GetMapping("/username/{usuario}")
    public Usuarios obtenerUsuario(@PathVariable("usuario") String usuario) {
        return usuarioServiceImpl.obtenerUsuario(usuario);
    }

    // 5. EDITAR (Combina HashService con manejo de excepciones detallado)
    @PutMapping("/{hash}")
    public ResponseEntity<?> editarUsuario(@PathVariable("hash") String hash, @RequestBody Usuarios usuario) {
        try {
            Integer idReal = hashService.decrypt(hash);
            usuario.setIdUsuario(idReal); // Asigna el ID real desencriptado

            Usuarios usuarioActualizado = usuarioServiceImpl.actualizarUsuario(usuario);
            return ResponseEntity.ok(usuarioActualizado);

        } catch (CorreoYaRegistradoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está registrado.");
        } catch (UsuarioYaRegistradoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya está registrado.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al editar el usuario.");
        }
    }
  
    // 6. ELIMINAR (Usa HashService)
    @DeleteMapping("/{hash}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            usuarioServiceImpl.eliminarUsuario(idReal);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 7. PERMISOS (Usa HashService)
    @GetMapping("/{hash}/permisos")
    public ResponseEntity<List<String>> obtenerPermisosPorUsuario(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            List<String> permisos = usuarioService.obtenerPermisosPorUsuarioId(idReal);
            return ResponseEntity.ok(permisos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 8. CAMBIAR PASSWORD (Usa HashService)
    @PutMapping("/{hash}/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@PathVariable("hash") String hash, @RequestBody String nuevaPassword) {
        try {
            Integer idReal = hashService.decrypt(hash);
            Optional<Usuarios> optional = usuarioServiceImpl.getUsuariosRepository().findById(idReal);
    
            if (optional.isPresent()) {
                Usuarios usuario = optional.get();
                // Limpia comillas y encripta
                String passwordEncriptada = bCryptPasswordEncoder.encode(nuevaPassword.replace("\"", ""));
                usuario.setPassword(passwordEncriptada);
                usuarioServiceImpl.getUsuariosRepository().save(usuario);
    
                return ResponseEntity.ok("Contraseña actualizada correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cambiar la contraseña.");
        }
    }
}