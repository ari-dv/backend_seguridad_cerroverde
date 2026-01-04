package com.alexander.sistema_cerro_verde_backend.firma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Usuarios;

@RestController
@RequestMapping("/cerro-verde/firma")
@CrossOrigin("*")
public class PdfFirmaController {

    @Autowired
    private PdfFirmaService pdfFirmaService;

    @PostMapping("/pdf")
        public ResponseEntity<byte[]> firmarPdf(
                @RequestParam("archivo") MultipartFile archivo,
                Authentication authentication
        ) throws Exception {

            Usuarios usuario = (Usuarios) authentication.getPrincipal();

            String firmante =
                    usuario.getNombre() + " " + usuario.getApellidos() + "\n" +
                    "Fecha: " + java.time.LocalDate.now() + "\n" +
                    "Hora: " + java.time.LocalTime.now().withNano(0);

            byte[] pdfFirmado = pdfFirmaService.firmarPdf(
                    archivo.getBytes(),
                    firmante
            );

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=documento_firmado.pdf"
                    )
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfFirmado);
        }

}
