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

import com.alexander.sistema_cerro_verde_backend.entity.compras.CategoriasProductos;
import com.alexander.sistema_cerro_verde_backend.service.compras.jpa.CategoriasProductosService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*")
public class CategoriasProductosController {

    @Autowired
    private CategoriasProductosService serviceCategoriasProductos;

    @Autowired
    private HashService hashService;

    @GetMapping("/categoriasproductos")
    public List<CategoriasProductos> buscarTodos(){
        return serviceCategoriasProductos.buscarTodos(); //findAll
    }

    @PostMapping("/categoriasproductos")
    public CategoriasProductos guardar (@RequestBody CategoriasProductos categoriaproducto){
        serviceCategoriasProductos.guardar(categoriaproducto);
        return categoriaproducto;
    }

    @PutMapping("/categoriasproductos/{hash}")
    public ResponseEntity<?> modificar(@PathVariable("hash") String hash, @RequestBody CategoriasProductos categoriaproducto) {
        try {
            Integer idReal = hashService.decrypt(hash);
            // Asegúrate que el setter coincida con tu entidad (ej: setId_categoria)
            categoriaproducto.setId_categoria(idReal);
            
            serviceCategoriasProductos.modificar(categoriaproducto);
            return ResponseEntity.ok(categoriaproducto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/categoriasproductos/{hash}")
    public ResponseEntity<CategoriasProductos> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceCategoriasProductos.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/categoriasproductos/{hash}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceCategoriasProductos.eliminar(idReal);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Categoria eliminada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }
}