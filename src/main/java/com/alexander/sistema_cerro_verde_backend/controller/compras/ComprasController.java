package com.alexander.sistema_cerro_verde_backend.controller.compras;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.alexander.sistema_cerro_verde_backend.entity.compras.Compras;
import com.alexander.sistema_cerro_verde_backend.service.compras.IComprasService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*")
public class ComprasController {

    @Autowired
    private IComprasService serviceCompras;

    @Autowired
    private HashService hashService;

    @GetMapping("/compras")
    public List<Compras> buscarTodos() {
        return serviceCompras.buscarTodos(); //findAll
    }

    @PostMapping("/compras")
    public Compras guardar(@RequestBody Compras compra) {
        serviceCompras.guardar(compra);
        return compra;
    }

    @PutMapping("/compras/{hash}")
    public ResponseEntity<?> modificar(@PathVariable("hash") String hash, @RequestBody Compras compra) {
        try {
            Integer idReal = hashService.decrypt(hash);
            // Asegúrate que el setter en tu entidad sea setId_compra
            compra.setId_compra(idReal); 
            
            serviceCompras.modificar(compra);
            return ResponseEntity.ok(compra);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/compras/{hash}")
    public ResponseEntity<Compras> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceCompras.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/compras/{hash}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceCompras.eliminar(idReal);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Compra eliminada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/datos-nuevacompra")
    public Map<String, String> obtenerDatosNuevaCompra() {
        Map<String, String> datos = new HashMap<>();
        datos.put("correlativo", serviceCompras.obtenerProximoCorrelativo());
        return datos;
    }
}