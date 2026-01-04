package com.alexander.sistema_cerro_verde_backend.controller.seguridad;


import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.alexander.sistema_cerro_verde_backend.config.JwtUtils;
import com.alexander.sistema_cerro_verde_backend.entity.seguridad.*;
import com.alexander.sistema_cerro_verde_backend.excepciones.UsuarioDeshabilitadoException;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.UserDetailsServiceImpl;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.jpa.CodigoVerificacionService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.jpa.UsuariosService;

@RestController
@CrossOrigin("*")
@RequestMapping("/cerro-verde")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UsuariosService usuariosService;
    private final CodigoVerificacionService codigoVerificacionService;
    private final JwtUtils jwtUtils;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserDetailsServiceImpl userDetailsService,
            UsuariosService usuariosService,
            CodigoVerificacionService codigoVerificacionService,
            JwtUtils jwtUtils) {

        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuariosService = usuariosService;
        this.codigoVerificacionService = codigoVerificacionService;
        this.jwtUtils = jwtUtils;
    }

    // ================= PASO 1 =================
    // LOGIN → ENVÍA CÓDIGO AL CORREO
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody JwtRequest request) throws Exception {

    Usuarios usuario = usuariosService.obtenerUsuario(request.getCorreo());

    if (usuario == null) {
        // Correo no registrado
        throw new UsernameNotFoundException("Correo incorrecto");
    }

    if (!usuario.isEnabled()) {
        throw new UsuarioDeshabilitadoException("Usuario deshabilitado");
    }

    try {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getCorreo(),
                request.getPassword()
            )
        );
    } catch (BadCredentialsException e) {
        // Contraseña incorrecta
        throw new BadCredentialsException("Contraseña incorrecta");
    }

    // Enviar código
    codigoVerificacionService.enviarCodigoVerificacion(request.getCorreo());

    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Código de verificación enviado al correo");
    response.put("token", ""); 
    return ResponseEntity.ok(response);
}

    @PostMapping("/validar-codigo")
    public ResponseEntity<?> validarCodigo(@RequestBody CodigoRequest request) {

        codigoVerificacionService.validarCodigo(
                request.getCorreo(),
                request.getCodigo()
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getCorreo());

        String token = jwtUtils.generateToken(userDetails);

        Usuarios usuario = usuariosService.obtenerUsuario(request.getCorreo());
        usuario.setToken(token);

        try {
            usuariosService.actualizarUsuario(usuario);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error al actualizar el usuario: " + e.getMessage());
        }

        return ResponseEntity.ok(new JwtResponse(token));
    }

    // ================= UTILS =================
    private void autenticar(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (DisabledException e) {
            throw new UsuarioDeshabilitadoException("Usuario deshabilitado");
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciales inválidas");
        }
    }

    @GetMapping("/usuario-actual")
    public Usuarios obtenerUsuarioActual(Principal principal) {
        return (Usuarios) userDetailsService.loadUserByUsername(principal.getName());
    }
}
