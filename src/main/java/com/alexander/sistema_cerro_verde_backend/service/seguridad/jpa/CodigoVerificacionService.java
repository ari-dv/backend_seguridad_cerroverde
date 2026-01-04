package com.alexander.sistema_cerro_verde_backend.service.seguridad.jpa;

import java.security.SecureRandom;
import java.time.LocalDateTime;
// Después (correcto)
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Usuarios;
import com.alexander.sistema_cerro_verde_backend.repository.seguridad.UsuariosRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class CodigoVerificacionService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private JavaMailSender mailSender;

    // 🔢 Genera código seguro de 6 dígitos
    private String generarCodigo6Digitos() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    @Async
    public void enviarCodigoVerificacion(String email) {

        Usuarios usuario = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("El correo no está registrado"));

        String codigo = generarCodigo6Digitos();

        usuario.setCodigoVerificacion(codigo);
        usuario.setCodigoVerificacionExpira(LocalDateTime.now().plusMinutes(10));

        usuariosRepository.save(usuario);

        // 🌟 Enviar correo en HTML
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(usuario.getEmail());
            helper.setSubject("Código de verificación");

 String htmlMsg = "<!DOCTYPE html>" +
"<html>" +
"<head>" +
"<meta charset='UTF-8'>" +
"<style>" +
"  body { margin: 0; padding: 0; font-family: 'Helvetica Neue', Arial, sans-serif; background-color: #eaf4ea; }" +
"  .email-container { max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 15px; overflow: hidden; box-shadow: 0 8px 20px rgba(0,0,0,0.12); }" +
"  .header { background: linear-gradient(90deg, #1a4d2b, #4caf50); padding: 35px 20px; text-align: center; color: #fff; }" +
"  .header img { width: 90px; margin-bottom: 15px; }" +
"  .header h1 { margin: 0; font-size: 28px; font-weight: 700; }" +
"  .content { padding: 35px 25px; text-align: center; color: #333; }" +
"  .content h2 { font-size: 24px; color: #1a4d2b; margin-bottom: 15px; }" +
"  .content p { font-size: 16px; color: #555; margin: 10px 0; line-height: 1.5; }" +
"  .code { font-size: 42px; font-weight: bold; color: #4caf50; margin: 25px 0; letter-spacing: 6px; }" +
"  .note { font-size: 14px; color: #777; margin-bottom: 30px; }" +
"  .button { display: inline-block; padding: 16px 32px; background: #4caf50; color: #ffffff !important; text-decoration: none; border-radius: 10px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); transition: all 0.3s ease; }" +
"  .button:hover { background: #1a4d2b; transform: translateY(-2px); box-shadow: 0 6px 18px rgba(0,0,0,0.15); }" +
"  .footer { background: #eaf4ea; padding: 25px; text-align: center; font-size: 12px; color: #555; }" +
"</style>" +
"</head>" +
"<body>" +
"<div class='email-container'>" +

"  <div class='header'>" +
"    <h1>Hotel Cerro Verde</h1>" +
"  </div>" +

"  <div class='content'>" +
"    <h2>¡Tu código de verificación!</h2>" +
"    <p>Para continuar con tu acceso, ingresa el siguiente código:</p>" +
"    <div class='code'>" + codigo + "</div>" +
"    <p class='note'>Este código es válido por 10 minutos. Si no solicitaste este código, ignora este correo.</p>" +
"    <a class='button' href='#'>Ingresar código</a>" +
"  </div>" +

"  <div class='footer'>" +
"    © 2026 Hotel Cerro Verde. Todos los derechos reservados." +
"  </div>" +

"</div>" +
"</body>" +
"</html>";




            helper.setText(htmlMsg, true); // true indica que es HTML
            mailSender.send(mensaje);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }

    // ✅ Validar código ingresado por el usuario
    public void validarCodigo(String email, String codigoIngresado) {

        Usuarios usuario = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getCodigoVerificacion() == null) {
            throw new RuntimeException("No existe un código activo");
        }

        if (!usuario.getCodigoVerificacion().equals(codigoIngresado)) {
            throw new RuntimeException("Código incorrecto");
        }

        if (usuario.getCodigoVerificacionExpira().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El código ha expirado");
        }

        // 🧹 Limpiar código después de usarlo
        usuario.setCodigoVerificacion(null);
        usuario.setCodigoVerificacionExpira(null);
        usuariosRepository.save(usuario);
    }
}
