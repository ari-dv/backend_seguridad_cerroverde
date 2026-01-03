package com.alexander.sistema_cerro_verde_backend.controller.ventas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexander.sistema_cerro_verde_backend.service.seguridad.HashService;
import com.alexander.sistema_cerro_verde_backend.service.ventas.NotaCreditoService;
import com.alexander.sistema_cerro_verde_backend.entity.ventas.NotaCredito;

@RestController
@RequestMapping("/cerro-verde")
@CrossOrigin("*")
public class NotaCreditoController {

    @Autowired
    private NotaCreditoService notaCreditoService;

    @Autowired
    private HashService hashService;

    // Busca la nota de crédito usando el Hash de la VENTA
    @GetMapping("/notaCredito/porVenta/{hashVenta}")
    public ResponseEntity<NotaCredito> obtenerPorVenta(@PathVariable("hashVenta") String hashVenta) {
        try {
            Integer idVenta = hashService.decrypt(hashVenta);
            return notaCreditoService.buscarPorVenta(idVenta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Descarga usando el Hash de la NOTA DE CRÉDITO
    @GetMapping("/notaCredito/{hash}/descargar")
    public ResponseEntity<byte[]> descargarNotaCredito(@PathVariable("hash") String hash) {
        
        try {
            Integer idReal = hashService.decrypt(hash);
            System.out.println("ID desencriptado para descarga: " + idReal);

            NotaCredito notaCredito = notaCreditoService.buscarId(idReal)
                    .orElseThrow(() -> new RuntimeException("Nota de crédito no encontrada"));

            byte[] pdfBytes = notaCredito.getPdfBytes();

            System.out.println("¿PDF existe? " + (pdfBytes != null));
            System.out.println("Tamaño del PDF: " + (pdfBytes != null ? pdfBytes.length : 0));

            if (pdfBytes == null || pdfBytes.length == 0) {
                throw new RuntimeException("PDF no generado o vacío");
            }

            HttpHeaders headers = new HttpHeaders();
            // Usamos el hash en el nombre del archivo para no revelar el ID tampoco al descargar
            headers.add("Content-Disposition", "attachment; filename=nota_credito_" + hash + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.internalServerError().body(null);
        }
    }
}