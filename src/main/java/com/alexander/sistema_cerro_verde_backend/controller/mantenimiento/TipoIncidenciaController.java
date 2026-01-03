package com.alexander.sistema_cerro_verde_backend.controller.mantenimiento;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.alexander.sistema_cerro_verde_backend.entity.mantenimiento.TipoIncidencia;
import com.alexander.sistema_cerro_verde_backend.repository.mantenimiento.IncidenciasRepository;
import com.alexander.sistema_cerro_verde_backend.service.mantenimiento.jpa.TipoIncidenciaService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde/tipoincidencia")
@CrossOrigin("*")
public class TipoIncidenciaController {
    
    @Autowired
    private TipoIncidenciaService serviceTipoIncidencia;
    
    @Autowired
    private IncidenciasRepository incidenciasRepository;

    @Autowired
    private HashService hashService;

    @GetMapping("/ver") //Ver
    public List<TipoIncidencia> buscarTodos() {
        return serviceTipoIncidencia.buscarTodos();
    }

    @GetMapping("/tipoincidencia/{hash}") //Ver por Hash
    public ResponseEntity<TipoIncidencia> buscarPorId(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceTipoIncidencia.buscarPorId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrar") //Registrar
    public TipoIncidencia registrar(@RequestBody TipoIncidencia tipoincidencia) {
        serviceTipoIncidencia.registrar(tipoincidencia);
        return tipoincidencia;
    }

    @PutMapping("/actualizar/{hash}") //Actualizar
    public ResponseEntity<?> actualizar (@PathVariable("hash") String hash, @RequestBody TipoIncidencia tipoincidencia){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceTipoIncidencia.actualizar(idReal, tipoincidencia);
            return ResponseEntity.ok("Tipo de incidencia actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{hash}")
    public ResponseEntity<?> eliminarTipoIncidencia(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            
            Optional<TipoIncidencia> tipoOpt = serviceTipoIncidencia.buscarPorId(idReal);
            if (tipoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TipoIncidencia tipo = tipoOpt.get();
            if (incidenciasRepository.existsByTipoIncidencia(tipo)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar este tipo de incidencia porque tiene incidencias asociadas.");
            }

            serviceTipoIncidencia.eliminarPorId(idReal); 
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}