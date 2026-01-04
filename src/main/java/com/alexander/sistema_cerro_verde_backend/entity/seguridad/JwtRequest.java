package com.alexander.sistema_cerro_verde_backend.entity.seguridad;

public class JwtRequest {
    
    private String correo; 
    private String password;

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public JwtRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }
  
}
