package com.scholarstay.app.dto;

public class MatchDTO {
    private Long usuarioId;
    private String nombre;
    private String carrera;
    private String universidad;
    private String biografia;
    private Double porcentaje;
    private String avatar; // <-- CAMPO AÑADIDO

    public MatchDTO() {}

    public MatchDTO(Long usuarioId, String nombre, String carrera, String universidad,
                    String biografia, Double porcentaje) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.carrera = carrera;
        this.universidad = universidad;
        this.biografia = biografia;
        this.porcentaje = porcentaje;
    }

    // Constructor completo con avatar
    public MatchDTO(Long usuarioId, String nombre, String carrera, String universidad,
                    String biografia, Double porcentaje, String avatar) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.carrera = carrera;
        this.universidad = universidad;
        this.biografia = biografia;
        this.porcentaje = porcentaje;
        this.avatar = avatar;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getUniversidad() { return universidad; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }

    public Double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(Double porcentaje) { this.porcentaje = porcentaje; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}