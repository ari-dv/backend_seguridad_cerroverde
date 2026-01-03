package com.alexander.sistema_cerro_verde_backend.controller.compras;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexander.sistema_cerro_verde_backend.service.compras.IDetallesCompraService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class DetallesCompraController {
    
    @Autowired
    private IDetallesCompraService serviceDetallesCompra;

    @Autowired
    private HashService hashService;

    @DeleteMapping("/detallescompra/{hash}")
    public ResponseEntity<?> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceDetallesCompra.eliminarDetalleCompra(idReal);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}