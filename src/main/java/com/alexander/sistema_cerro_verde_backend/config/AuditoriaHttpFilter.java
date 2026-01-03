package com.alexander.sistema_cerro_verde_backend.config;

import com.alexander.sistema_cerro_verde_backend.entity.AuditoriaHttp;
import com.alexander.sistema_cerro_verde_backend.repository.AuditoriaHttpRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class AuditoriaHttpFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaHttpFilter.class);

    @Autowired
    private AuditoriaHttpRepository auditoriaRepository; // <--- Inyectamos el Repo

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            
            // Filtramos para no llenar la BD con basura (ej: ignorar imágenes o CSS si quisieras)
            // Si quieres guardar TODO, borra este if.
            String uri = request.getRequestURI();
            
            // Capturamos datos clave
            String method = request.getMethod();
            String user = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonimo";
            String ip = request.getRemoteAddr();

            // LOGICA: Guardamos todo en BD, o solo lo importante.
            // Recomendación: Guardar todo lo que sea API (/cerro-verde/...)
            if (uri.contains("/cerro-verde/")) {
                
                AuditoriaHttp log = new AuditoriaHttp(user, method, uri, ip, status, duration);
                
                try {
                    auditoriaRepository.save(log); // <--- AQUÍ SE GUARDA EN LA BD
                } catch (Exception e) {
                    logger.error("No se pudo guardar la auditoría: " + e.getMessage());
                }

                // Dejamos el log de consola también porque es útil para desarrollar
                if (status >= 400) {
                    logger.warn("🚨 ALERTA BD | Status: {} | URL: {}", status, uri);
                }
            }
        }
    }
}