package com.alexander.sistema_cerro_verde_backend.service.seguridad.jpa;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexander.sistema_cerro_verde_backend.entity.seguridad.PasswordResetToken;
import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Usuarios;
import com.alexander.sistema_cerro_verde_backend.repository.seguridad.PasswordResetTokenRepository;
import com.alexander.sistema_cerro_verde_backend.repository.seguridad.UsuariosRepository;
import com.alexander.sistema_cerro_verde_backend.service.seguridad.IPasswordResetService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class PasswordResetService implements IPasswordResetService {

    private final UsuariosRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public PasswordResetService(UsuariosRepository usuarioRepository,
                                PasswordResetTokenRepository tokenRepository,
                                JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void enviarLinkRecuperacion(String email) {
        Usuarios usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("El correo no está registrado en el sistema."));

        // Eliminar tokens existentes
        tokenRepository.deleteByUsuario(usuario);
        tokenRepository.flush();

        // Crear nuevo token
        PasswordResetToken resetToken = crearToken(usuario);

        tokenRepository.save(resetToken);

        // Enviar correo HTML
        enviarCorreoRecuperacionHTML(usuario, resetToken.getToken());
    }

    /**
     * Genera un token de recuperación para un usuario.
     */
    private PasswordResetToken crearToken(Usuarios usuario) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUsuario(usuario);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        return token;
    }

    /**
     * Envía un correo HTML con botón y diseño moderno al usuario.
     */
    private void enviarCorreoRecuperacionHTML(Usuarios usuario, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String urlReset = "http://localhost:4200/reset-password?token=" + token;

            String htmlMsg = "<html>"
                    + "<body style='font-family: Arial, sans-serif; background-color:#f7f7f7; padding:20px;'>"
                    + "<div style='max-width:600px; margin:auto; background-color:#ffffff; padding:30px; border-radius:10px; box-shadow:0 0 10px rgba(0,0,0,0.1);'>"
                    + "<h2 style='color:#333;'>Hola " + usuario.getNombre() + ",</h2>"
                    + "<p>Recibimos una solicitud para restablecer tu contraseña.</p>"
                    + "<p>Haz clic en el botón de abajo para restablecer tu contraseña:</p>"
                    + "<a href='" + urlReset + "' style='display:inline-block; padding:15px 25px; background-color:#007bff; color:#fff; text-decoration:none; border-radius:5px; font-weight:bold;'>Restablecer contraseña</a>"
                    + "<p style='margin-top:20px; font-size:12px; color:#666;'>Este enlace expirará en 30 minutos.</p>"
                    + "<hr>"
                    + "<p style='font-size:12px; color:#999;'>Si no solicitaste el cambio de contraseña, ignora este correo.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setTo(usuario.getEmail());
            helper.setSubject("Recuperación de contraseña");
            helper.setText(htmlMsg, true); // true = HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo de recuperación", e);
        }
    }
}