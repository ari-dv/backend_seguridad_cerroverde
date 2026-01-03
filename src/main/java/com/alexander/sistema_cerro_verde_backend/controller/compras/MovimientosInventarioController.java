package com.alexander.sistema_cerro_verde_backend.controller.compras;

import java.util.List;
import java.util.Optional;

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

import com.alexander.sistema_cerro_verde_backend.dto.compras.MovimientoInventarioDTO;
import com.alexander.sistema_cerro_verde_backend.entity.compras.MovimientosInventario;
import com.alexander.sistema_cerro_verde_backend.service.compras.IMovimientosInventarioService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*")
public class MovimientosInventarioController {
    
    @Autowired
    private IMovimientosInventarioService serviceMovimientosInventario;

    @Autowired
    private HashService hashService;

    @GetMapping("/movimientosinventario")
    public List<MovimientoInventarioDTO> buscarTodos() {
        return serviceMovimientosInventario.buscarTodos(); 
    }

    @PostMapping("/movimientosinventario")
    public MovimientosInventario guardar(@RequestBody MovimientosInventario movimientoinventario) {
        serviceMovimientosInventario.guardar(movimientoinventario);
        return movimientoinventario;
    }

    @PutMapping("/movimientosinventario/{hash}")
    public ResponseEntity<?> modificar(@PathVariable("hash") String hash, @RequestBody MovimientosInventario movimientoinventario) {
        try {
            Integer idReal = hashService.decrypt(hash);
            // Asegúrate que el setter coincida con tu entidad
            movimientoinventario.setId_movimiento_inventario(idReal);
            
            serviceMovimientosInventario.modificar(movimientoinventario);
            return ResponseEntity.ok(movimientoinventario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/movimientosinventario/{hash}")
    public ResponseEntity<MovimientoInventarioDTO> buscarId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceMovimientosInventario.buscarId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/movimientosinventario/eliminar/{hash}")
    public ResponseEntity<String> eliminar(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceMovimientosInventario.eliminar(idReal);
            return ResponseEntity.ok("Movimiento inventario eliminado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}