package com.scholarstay.app.dto;

import java.time.LocalDate;

public class ReservaDTO {
    private Long id;
    private String tituloAlojamiento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Double monto;
    private String nombreUsuario;

    public ReservaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTituloAlojamiento() { return tituloAlojamiento; }
    public void setTituloAlojamiento(String tituloAlojamiento) { this.tituloAlojamiento = tituloAlojamiento; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
}
