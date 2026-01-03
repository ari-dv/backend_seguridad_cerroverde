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

import com.alexander.sistema_cerro_verde_backend.entity.mantenimiento.PersonalLimpieza;
import com.alexander.sistema_cerro_verde_backend.repository.mantenimiento.LimpiezasRepository;
import com.alexander.sistema_cerro_verde_backend.service.mantenimiento.jpa.PersonalLimpiezaService;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;

@RestController
@RequestMapping("/cerro-verde/personallimpieza")
@CrossOrigin("*")
public class PersonalLimpiezaController {
    
    @Autowired
    private PersonalLimpiezaService servicePersonalLimpieza;

    @Autowired
    private LimpiezasRepository limpiezasRepository;

    @Autowired
    private HashService hashService;
    
    @GetMapping("/ver") //Ver
    public List<PersonalLimpieza> buscarTodos() {
        return servicePersonalLimpieza.buscarTodos();
    }

    @GetMapping("/personallimpieza/{hash}") //Ver por Hash
    public ResponseEntity<PersonalLimpieza> buscarPorId(@PathVariable("hash") String hash){
        try {
            Integer idReal = hashService.decrypt(hash);
            return servicePersonalLimpieza.buscarPorId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrar") //Registrar
    public PersonalLimpieza registrar(@RequestBody PersonalLimpieza personallimpieza) {
        servicePersonalLimpieza.registrar(personallimpieza);
        return personallimpieza;
    }

    @PutMapping("/actualizar/{hash}") //Actualizar
    public ResponseEntity<?> actualizar (@PathVariable("hash") String hash, @RequestBody PersonalLimpieza personallimpieza){
        try {
            Integer idReal = hashService.decrypt(hash);
            servicePersonalLimpieza.actualizar(idReal, personallimpieza);
            return ResponseEntity.ok("Personal actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{hash}")
    public ResponseEntity<?> eliminarPersonalLimpieza(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);

            Optional<PersonalLimpieza> personalOpt = servicePersonalLimpieza.buscarPorId(idReal);
            if (personalOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            PersonalLimpieza personal = personalOpt.get();
            if (limpiezasRepository.existsByPersonal(personal)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el personal porque tiene limpiezas asociadas.");
            }

            servicePersonalLimpieza.eliminarPorId(idReal); 
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}