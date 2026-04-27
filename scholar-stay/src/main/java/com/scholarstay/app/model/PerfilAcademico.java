package com.scholarstay.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "perfiles_academicos")
public class PerfilAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carrera;
    private String horarioEstudio; // e.g., "Mañana", "Tarde", "Noche"
    private String habitosRuido; // e.g., "Silencio absoluto", "Música de fondo", "Ruido normal"
    private String habitosSueno; // e.g., "Madrugador", "Noctámbulo"
    private String nivelTolerancia; // e.g., "Alto", "Medio", "Bajo"
    private String biografia;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public PerfilAcademico() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getHorarioEstudio() { return horarioEstudio; }
    public void setHorarioEstudio(String horarioEstudio) { this.horarioEstudio = horarioEstudio; }

    public String getHabitosRuido() { return habitosRuido; }
    public void setHabitosRuido(String habitosRuido) { this.habitosRuido = habitosRuido; }

    public String getHabitosSueno() { return habitosSueno; }
    public void setHabitosSueno(String habitosSueno) { this.habitosSueno = habitosSueno; }

    public String getNivelTolerancia() { return nivelTolerancia; }
    public void setNivelTolerancia(String nivelTolerancia) { this.nivelTolerancia = nivelTolerancia; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
