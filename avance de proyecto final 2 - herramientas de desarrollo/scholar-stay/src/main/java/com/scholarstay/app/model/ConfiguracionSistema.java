package com.scholarstay.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracion_sistema")
public class ConfiguracionSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombrePlataforma;

    @Column(nullable = false)
    private Double precioMinimo;

    @Column(nullable = false)
    private Double precioMaximo;

    @Column(nullable = false)
    private Integer maxReservasPorUsuario;

    public ConfiguracionSistema() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombrePlataforma() { return nombrePlataforma; }
    public void setNombrePlataforma(String nombrePlataforma) { this.nombrePlataforma = nombrePlataforma; }

    public Double getPrecioMinimo() { return precioMinimo; }
    public void setPrecioMinimo(Double precioMinimo) { this.precioMinimo = precioMinimo; }

    public Double getPrecioMaximo() { return precioMaximo; }
    public void setPrecioMaximo(Double precioMaximo) { this.precioMaximo = precioMaximo; }

    public Integer getMaxReservasPorUsuario() { return maxReservasPorUsuario; }
    public void setMaxReservasPorUsuario(Integer maxReservasPorUsuario) { this.maxReservasPorUsuario = maxReservasPorUsuario; }
}