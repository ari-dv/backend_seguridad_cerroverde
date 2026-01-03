package com.alexander.sistema_cerro_verde_backend.controller.ventas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alexander.sistema_cerro_verde_backend.entity.ventas.Ventas;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;
import com.alexander.sistema_cerro_verde_backend.service.ventas.IVentaService;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*")
public class VentaController {

    @Autowired
    private IVentaService ventaService;

    @Autowired
    private HashService hashService;

    @RequestMapping("/venta")
    public List<Ventas> buscarTodos() {
        return ventaService.buscarTodos();
    }

    @RequestMapping("/venta/{hash}")
    public ResponseEntity<Ventas> buscarPorId(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);
            return ventaService.buscarPorId(idReal)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint para registrar pago de hospedaje
    @PostMapping("/venta/hospedaje")
    public ResponseEntity<Map<String, Object>> registrarPagoHospedaje(@RequestBody Ventas venta) {
        Map<String, Object> response = new HashMap<>();
        try {
            ventaService.registrarPagoHospedaje(venta);
            response.put("success", true);
            response.put("mensaje", "Pago de hospedaje registrado exitosamente");
            response.put("ventaId", venta.getIdVenta());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Endpoint para registrar venta de productos
    @PostMapping("/venta/productos")
    public ResponseEntity<Map<String, Object>> registrarVentaProductos(@RequestBody Ventas venta) {
        Map<String, Object> response = new HashMap<>();
        try {
            ventaService.registrarVentaProductos(venta);
            response.put("success", true);
            response.put("mensaje", "Venta de productos registrada exitosamente");
            response.put("ventaId", venta.getIdVenta());
            response.put("estado", "pendiente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Endpoint para editar venta de productos (solo pendientes)
    // OJO: Se agregó {hash} en la URL para seguridad
    @PutMapping("/venta/productos/{hash}")
    public ResponseEntity<Map<String, Object>> editarVentaProductos(
            @PathVariable("hash") String hash,
            @RequestBody Ventas venta) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Integer idReal = hashService.decrypt(hash);
            venta.setIdVenta(idReal); // Aseguramos el ID correcto

            ventaService.editarVentaProductos(venta);
            response.put("success", true);
            response.put("mensaje", "Venta de productos editada exitosamente");
            response.put("ventaId", venta.getIdVenta());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Endpoint para confirmar venta de productos (generar comprobante)
    @PutMapping("/venta/productos/{hash}/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarVentaProductos(
            @PathVariable("hash") String hash,
            @RequestParam String tipoComprobante) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Integer idReal = hashService.decrypt(hash);

            ventaService.confirmarVentaProductos(idReal, tipoComprobante);
            response.put("success", true);
            response.put("mensaje", "Venta confirmada y comprobante generado exitosamente");
            response.put("ventaId", idReal);
            response.put("estado", "completada");
            response.put("tipoComprobante", tipoComprobante);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/venta/{hash}")
    public ResponseEntity<?> eliminar(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);

            ventaService.eliminar(idReal);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Venta eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se pudo eliminar la venta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/pdf/{hash}")
    public ResponseEntity<byte[]> descargarComprobante(@PathVariable("hash") String hash) {
        try {
            Integer idReal = hashService.decrypt(hash);

            // Generar el PDF como arreglo de bytes
            byte[] pdfBytes = ventaService.generarPdf(idReal);

            // Configurar cabeceras HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            // Nombre del archivo dinámico usando el hash para no revelar ID
            String nombreArchivo = "comprobante_" + hash + ".pdf";

            headers.setContentDisposition(ContentDisposition.attachment().filename(nombreArchivo).build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}