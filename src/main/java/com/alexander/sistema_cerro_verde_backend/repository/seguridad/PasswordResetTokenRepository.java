package com.alexander.sistema_cerro_verde_backend.repository.seguridad;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alexander.sistema_cerro_verde_backend.entity.seguridad.PasswordResetToken;
import com.alexander.sistema_cerro_verde_backend.entity.seguridad.Usuarios;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    // Buscar token por valor
    Optional<PasswordResetToken> findByToken(String token);

    // Buscar token por usuario
    Optional<PasswordResetToken> findByUsuario(Usuarios usuario);

    // Eliminar token por usuario
    void deleteByUsuario(Usuarios usuario);
}
