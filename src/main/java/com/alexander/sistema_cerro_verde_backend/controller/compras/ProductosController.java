package com.alexander.sistema_cerro_verde_backend.controller.compras;

import java.util.Collections;
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

import com.alexander.sistema_cerro_verde_backend.entity.compras.Productos;
import com.alexander.sistema_cerro_verde_backend.service.compras.IProductosService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*")
public class ProductosController {
    
    @Autowired
    private IProductosService serviceProductos;

    @Autowired
    private HashService hashService;

    @GetMapping("/productos")
    public List<Productos> buscarTodos(){
        return serviceProductos.buscarTodos();
    }

    @PostMapping("/productos")
    public Productos guardar(@RequestBody Productos producto) {
        serviceProductos.guardar(producto);
        return producto;
    }

    @PutMapping("/productos/{hash}")
    public ResponseEntity<?> modificar(@PathVariable("hash") String hash, @RequestBody Productos producto) {
        try {
            Integer idReal = hashService.decrypt(hash);
            // Asegúrate que el setter coincida con tu entidad (ej: setId_producto)
            producto.setId_producto(idReal);
            
            serviceProductos.modificar(producto);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/productos/{hash}")
    public ResponseEntity<Productos> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceProductos.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/productos/{hash}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceProductos.eliminar(idReal);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Producto eliminado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }
}