package com.alexander.sistema_cerro_verde_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_http")
public class AuditoriaHttp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private String usuario;      // Quién (o "Anonimo")
    private String metodo;       // GET, POST, PUT, DELETE
    private String url;          // A dónde intentó entrar
    private String ip;           // Desde dónde (importante para detectar hackers)
    private Integer codigoEstado; // 200, 400, 401, 500
    private Long tiempoEjecucion; // Cuánto tardó (ms)

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    // Constructores, Getters y Setters
    public AuditoriaHttp() {}

    public AuditoriaHttp(String usuario, String metodo, String url, String ip, Integer codigoEstado, Long tiempoEjecucion) {
        this.usuario = usuario;
        this.metodo = metodo;
        this.url = url;
        this.ip = ip;
        this.codigoEstado = codigoEstado;
        this.tiempoEjecucion = tiempoEjecucion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getCodigoEstado() {
        return codigoEstado;
    }

    public void setCodigoEstado(Integer codigoEstado) {
        this.codigoEstado = codigoEstado;
    }

    public Long getTiempoEjecucion() {
        return tiempoEjecucion;
    }

    public void setTiempoEjecucion(Long tiempoEjecucion) {
        this.tiempoEjecucion = tiempoEjecucion;
    }

    
}