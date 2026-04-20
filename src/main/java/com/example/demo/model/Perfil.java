package com.example.demo.model;

public class Perfil {
    private String nombre;
    private String correo;
    private String biografia;
    private String universidad;
    private String carrera;
    private String nivelRuido;
    private String horarioEstudio;
    private boolean fuma;
    private boolean mascotas;

    public Perfil() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getUniversidad() {
        return universidad;
    }

    public void setUniversidad(String universidad) {
        this.universidad = universidad;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getNivelRuido() {
        return nivelRuido;
    }

    public void setNivelRuido(String nivelRuido) {
        this.nivelRuido = nivelRuido;
    }

    public String getHorarioEstudio() {
        return horarioEstudio;
    }

    public void setHorarioEstudio(String horarioEstudio) {
        this.horarioEstudio = horarioEstudio;
    }

    public boolean isFuma() {
        return fuma;
    }

    public void setFuma(boolean fuma) {
        this.fuma = fuma;
    }

    public boolean isMascotas() {
        return mascotas;
    }

    public void setMascotas(boolean mascotas) {
        this.mascotas = mascotas;
    }
    
}
