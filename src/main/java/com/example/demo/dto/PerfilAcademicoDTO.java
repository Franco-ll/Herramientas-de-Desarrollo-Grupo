package com.scholarstay.app.dto;

public class PerfilAcademicoDTO {
    private String nombre;
    private String email;
    private String biografia;
    private String carrera;
    private String horarioEstudio;
    private String habitosRuido;
    private String habitosSueno;
    private String nivelTolerancia;

    public PerfilAcademicoDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

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
}
