package com.alexander.sistema_cerro_verde_backend.controller.ventas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

import com.alexander.sistema_cerro_verde_backend.entity.ventas.MetodosPago;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;
import com.alexander.sistema_cerro_verde_backend.service.ventas.IMetodoPagoService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*")
public class MetodoPagoController {

    @Autowired
    private IMetodoPagoService metodoService;

    @Autowired
    private HashService hashService;

    @GetMapping("/metodopago")
    public List<MetodosPago> buscarTodos() { //Listar todos los métodos de pago
        return metodoService.buscarTodos();
    }

    @GetMapping("/metodopago/{hash}")
    public ResponseEntity<MetodosPago> buscarPorId(@PathVariable("hash") String hash) { //Buscar por hash
        try {
            Integer idReal = hashService.decrypt(hash);
            return metodoService.buscarPorId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/metodopago")
    public MetodosPago registrar(@RequestBody MetodosPago metodo) { //Registrar método de pago
        metodoService.registrar(metodo);
        return metodo;
    }

    @PutMapping("/metodopago/{hash}")
    public ResponseEntity<?> modificar(@PathVariable("hash") String hash, @RequestBody MetodosPago metodo) { //Modificar
        try {
            // Integer idReal = hashService.decrypt(hash);
            // Asegúrate de que el setter coincida con tu entidad (ej: setId_metodo_pago o setId)
            // metodo.setId_metodo_pago(idReal); 
            
            metodoService.registrar(metodo); // Usas registrar para modificar, según tu lógica original
            return ResponseEntity.ok(metodo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/metodopago/{hash}")
    public ResponseEntity<?> eliminar(@PathVariable("hash") String hash) { //Eliminar por hash
        try {
            Integer idReal = hashService.decrypt(hash);
            
            metodoService.eliminar(idReal);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Método de Pago eliminado correctamente");
            return ResponseEntity.ok(response);
            
        } catch (DataIntegrityViolationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            // Mantenemos tu mensaje original de error
            response.put("mensaje", "Ocurrió un problema. Vuelva a intentarlo"); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}