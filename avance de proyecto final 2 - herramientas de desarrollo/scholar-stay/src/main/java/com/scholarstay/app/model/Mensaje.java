package com.scholarstay.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversacion_id", nullable = false)
    private Conversacion conversacion;

    @ManyToOne
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    @Column(nullable = false, length = 2000)
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime fecha;

    private LocalDateTime fechaEdicion;

    @Column(nullable = false)
    private boolean leido = false;

    @Column(nullable = false)
    private boolean eliminado = false;

    public Mensaje() {
        this.fecha = LocalDateTime.now();
    }

    public Mensaje(Conversacion conversacion, Usuario remitente, String contenido) {
        this.conversacion = conversacion;
        this.remitente = remitente;
        this.contenido = contenido;
        this.fecha = LocalDateTime.now();
        this.leido = false;
        this.eliminado = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Conversacion getConversacion() { return conversacion; }
    public void setConversacion(Conversacion conversacion) { this.conversacion = conversacion; }

    public Usuario getRemitente() { return remitente; }
    public void setRemitente(Usuario remitente) { this.remitente = remitente; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public LocalDateTime getFechaEdicion() { return fechaEdicion; }
    public void setFechaEdicion(LocalDateTime fechaEdicion) { this.fechaEdicion = fechaEdicion; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
}
