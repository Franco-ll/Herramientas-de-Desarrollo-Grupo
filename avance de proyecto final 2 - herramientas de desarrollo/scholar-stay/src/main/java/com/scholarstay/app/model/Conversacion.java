package com.scholarstay.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversaciones")
public class Conversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario1_id", nullable = false)
    private Usuario usuario1;

    @ManyToOne
    @JoinColumn(name = "usuario2_id", nullable = false)
    private Usuario usuario2;

    @Column(length = 500)
    private String ultimoMensaje;

    private LocalDateTime fechaUltimoMensaje;

    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean eliminadoPorUsuario1 = false;

    @Column(nullable = false)
    private boolean eliminadoPorUsuario2 = false;

    public Conversacion() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Conversacion(Usuario usuario1, Usuario usuario2) {
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario1() { return usuario1; }
    public void setUsuario1(Usuario usuario1) { this.usuario1 = usuario1; }

    public Usuario getUsuario2() { return usuario2; }
    public void setUsuario2(Usuario usuario2) { this.usuario2 = usuario2; }

    public String getUltimoMensaje() { return ultimoMensaje; }
    public void setUltimoMensaje(String ultimoMensaje) { this.ultimoMensaje = ultimoMensaje; }

    public LocalDateTime getFechaUltimoMensaje() { return fechaUltimoMensaje; }
    public void setFechaUltimoMensaje(LocalDateTime fechaUltimoMensaje) { this.fechaUltimoMensaje = fechaUltimoMensaje; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean isEliminadoPorUsuario1() { return eliminadoPorUsuario1; }
    public void setEliminadoPorUsuario1(boolean eliminadoPorUsuario1) { this.eliminadoPorUsuario1 = eliminadoPorUsuario1; }

    public boolean isEliminadoPorUsuario2() { return eliminadoPorUsuario2; }
    public void setEliminadoPorUsuario2(boolean eliminadoPorUsuario2) { this.eliminadoPorUsuario2 = eliminadoPorUsuario2; }
}
