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

import com.alexander.sistema_cerro_verde_backend.entity.ventas.Clientes;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;
import com.alexander.sistema_cerro_verde_backend.service.ventas.ApiCliente;
import com.alexander.sistema_cerro_verde_backend.service.ventas.ClientesService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*")
public class ClientesController {

    @Autowired
    private ClientesService serviceClientes;
    
    @Autowired
    private ApiCliente api;

    @Autowired
    private HashService hashService;

    @GetMapping("/clientes")
    public List<Clientes> buscarTodos() {
        return serviceClientes.buscarTodos();
    }

    @GetMapping("/clientes/{hash}")
    public ResponseEntity<Clientes> buscarPorId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceClientes.buscarPorId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/clientes")
    public Clientes guardar(@RequestBody Clientes cliente) {
        serviceClientes.guardar(cliente);
        return cliente;
    }

    @PutMapping("/clientes/{hash}")
    public ResponseEntity<?> modificar(@PathVariable("hash") String hash, @RequestBody Clientes cliente) {
        try {
            Integer idReal = hashService.decrypt(hash);
            
            // --- VERIFICA QUE ESTE SETTER SEA CORRECTO ---
            cliente.setIdCliente(idReal); 
            // ---------------------------------------------

            serviceClientes.modificar(cliente);
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/clientes/eliminar/{hash}")
    public ResponseEntity<?> eliminar(@PathVariable("hash") String hash) {
        Map<String, String> response = new HashMap<>();
        try {
            Integer idReal = hashService.decrypt(hash);
            
            serviceClientes.eliminar(idReal);
            
            response.put("mensaje", "Cliente eliminado correctamente");
            return ResponseEntity.ok(response);
            
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "No se puede eliminar porque tiene registros asociados.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al procesar la solicitud (ID inválido o error interno).");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Map<String, String>> buscarDni(@PathVariable("dni") String dni) {
        // El DNI no se encripta con HashService porque es un dato público/búsqueda, no un ID de base de datos
        String resultado = api.consumirApi(dni);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("datos", resultado);
        return ResponseEntity.ok(respuesta);
    }
}