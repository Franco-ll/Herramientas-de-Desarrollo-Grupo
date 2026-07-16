package com.scholarstay.app.dto;

public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String carrera;
    private String avatar;
    private String propiedadAsignada;
    private String estadoReserva;


    public UsuarioDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getPropiedadAsignada() { return propiedadAsignada; }
    public void setPropiedadAsignada(String propiedadAsignada) { this.propiedadAsignada = propiedadAsignada; }

    public String getEstadoReserva() { return estadoReserva; }
    public void setEstadoReserva(String estadoReserva) { this.estadoReserva = estadoReserva; }


}