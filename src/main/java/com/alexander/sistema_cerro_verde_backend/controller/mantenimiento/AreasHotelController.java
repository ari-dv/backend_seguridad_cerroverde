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

import com.alexander.sistema_cerro_verde_backend.entity.mantenimiento.AreasHotel;
import com.alexander.sistema_cerro_verde_backend.repository.mantenimiento.IncidenciasRepository;
import com.alexander.sistema_cerro_verde_backend.service.mantenimiento.jpa.AreasHotelService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde/areashotel")
@CrossOrigin("*")
public class AreasHotelController {
    
    @Autowired
    private AreasHotelService serviceAreasHotel;

    @Autowired
    private IncidenciasRepository incidenciasRepository;
    
    @Autowired
    private HashService hashService;
    
    @GetMapping("/ver") //Ver
    public List<AreasHotel> buscarTodos() {
        return serviceAreasHotel.buscarTodos();
    }

    @GetMapping("/areashotel/{hash}") //Ver por Hash
    public ResponseEntity<AreasHotel> buscarPorId(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            return serviceAreasHotel.buscarPorId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrar") //Registrar
    public AreasHotel registrar(@RequestBody AreasHotel areashotel) {
        serviceAreasHotel.registrar(areashotel);
        return areashotel;
    }

    @PutMapping("/actualizar/{hash}") //Actualizar
    public ResponseEntity<?> actualizar (@PathVariable("hash") String hash, @RequestBody AreasHotel areashotel){
        try {
            Integer idReal = hashService.decrypt(hash);
            serviceAreasHotel.actualizar(idReal, areashotel);
            return ResponseEntity.ok("Área actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{hash}") //Eliminar
    public ResponseEntity<?> eliminarPorId (@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            
            Optional<AreasHotel> areaOpt = serviceAreasHotel.buscarPorId(idReal);
            if (areaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            AreasHotel area = areaOpt.get();

            if (incidenciasRepository.existsByArea(area)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el área, tiene incidencias asociadas.");
            }
            
            serviceAreasHotel.eliminarPorId(idReal);

            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
             return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}