package com.scholarstay.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "log_actividad")
public class LogActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String adminNombre;

    @Column(nullable = false)
    private String adminEmail;

    @Column(nullable = false)
    private String accion;

    @Column(nullable = false)
    private String detalle;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public LogActividad() {}

    public LogActividad(String adminNombre, String adminEmail, String accion, String detalle) {
        this.adminNombre = adminNombre;
        this.adminEmail = adminEmail;
        this.accion = accion;
        this.detalle = detalle;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getAdminNombre() { return adminNombre; }
    public void setAdminNombre(String adminNombre) { this.adminNombre = adminNombre; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}