package com.scholarstay.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "grupos_comunidad")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Carrera asociada (opcional) para facilitar filtros
    private String carrera;

    // Intereses o tags separados por comas
    private String intereses;

    private Integer miembros = 0;

    public Grupo() {}

    public Grupo(String nombre, String descripcion, String carrera, String intereses, Integer miembros) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.carrera = carrera;
        this.intereses = intereses;
        this.miembros = miembros == null ? 0 : miembros;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getIntereses() { return intereses; }
    public void setIntereses(String intereses) { this.intereses = intereses; }

    public Integer getMiembros() { return miembros; }
    public void setMiembros(Integer miembros) { this.miembros = miembros; }
}
