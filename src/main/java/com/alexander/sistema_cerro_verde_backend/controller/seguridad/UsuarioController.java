package com.alexander.sistema_cerro_verde_backend.controller.seguridad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @GetMapping("/")
    public List<Usuarios> listarUsuarios(){
        return usuarioServiceImpl.obtenerTodosUsuarios();
    }

    @PostMapping("/")
    public Usuarios guardUsuario(@RequestBody Usuarios usuario) throws Exception {
        usuario.setPassword(this.bCryptPasswordEncoder.encode(usuario.getPassword()));
        usuario.setIdUsuario(null); 
        
        if (usuario.getRol() == null) {
            Roles rolPorDefecto = new Roles();
            rolPorDefecto.setId(1); 
            usuario.setRol(rolPorDefecto);
        }
        
        return usuarioService.guardarUsuario(usuario);
    }
        
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

    @GetMapping("/username/{usuario}")
    public Usuarios obtenerUsuario(@PathVariable("usuario") String usuario) {
        return usuarioServiceImpl.obtenerUsuario(usuario);
    }

    @PutMapping("/{hash}")
    public ResponseEntity<?> editarUsuario(@PathVariable("hash") String hash, @RequestBody Usuarios usuario) {
        try {
            Integer idReal = hashService.decrypt(hash);
            usuario.setIdUsuario(idReal);

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

    @PutMapping("/{hash}/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@PathVariable("hash") String hash, @RequestBody String nuevaPassword) {
        try {
            Integer idReal = hashService.decrypt(hash);
            Optional<Usuarios> optional = usuarioServiceImpl.getUsuariosRepository().findById(idReal);
    
            if (optional.isPresent()) {
                Usuarios usuario = optional.get();
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