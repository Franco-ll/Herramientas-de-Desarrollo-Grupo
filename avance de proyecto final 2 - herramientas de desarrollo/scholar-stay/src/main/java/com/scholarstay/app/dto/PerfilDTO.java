package com.scholarstay.app.dto;

/**
 * DTO para transferir datos completos de perfil académico a las vistas.
 * Utilizado para cargar perfiles dinámicos en las vistas Thymeleaf.
 */
public class PerfilDTO {
    private Long usuarioId;
    private String nombre;
    private String carrera;
    private String universidad;
    private String ciclo;
    private String biografia;
    private String intereses;
    private String horarioEstudio;
    private String habitosRuido;
    private String habitosSueno;
    private String nivelTolerancia;
    private String estiloVida;
    private Double porcentajeCompatibilidad;

    public PerfilDTO() {}

    public PerfilDTO(Long usuarioId, String nombre, String carrera, String universidad,
                     String ciclo, String biografia, String intereses, String horarioEstudio,
                     String habitosRuido, String habitosSueno, String nivelTolerancia) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.carrera = carrera;
        this.universidad = universidad;
        this.ciclo = ciclo;
        this.biografia = biografia;
        this.intereses = intereses;
        this.horarioEstudio = horarioEstudio;
        this.habitosRuido = habitosRuido;
        this.habitosSueno = habitosSueno;
        this.nivelTolerancia = nivelTolerancia;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getUniversidad() { return universidad; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }

    public String getCiclo() { return ciclo; }
    public void setCiclo(String ciclo) { this.ciclo = ciclo; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public String getIntereses() { return intereses; }
    public void setIntereses(String intereses) { this.intereses = intereses; }

    public String getHorarioEstudio() { return horarioEstudio; }
    public void setHorarioEstudio(String horarioEstudio) { this.horarioEstudio = horarioEstudio; }

    public String getHabitosRuido() { return habitosRuido; }
    public void setHabitosRuido(String habitosRuido) { this.habitosRuido = habitosRuido; }

    public String getHabitosSueno() { return habitosSueno; }
    public void setHabitosSueno(String habitosSueno) { this.habitosSueno = habitosSueno; }

    public String getNivelTolerancia() { return nivelTolerancia; }
    public void setNivelTolerancia(String nivelTolerancia) { this.nivelTolerancia = nivelTolerancia; }

    public String getEstiloVida() { return estiloVida; }
    public void setEstiloVida(String estiloVida) { this.estiloVida = estiloVida; }

    public Double getPorcentajeCompatibilidad() { return porcentajeCompatibilidad; }
    public void setPorcentajeCompatibilidad(Double porcentajeCompatibilidad) { this.porcentajeCompatibilidad = porcentajeCompatibilidad; }
}
