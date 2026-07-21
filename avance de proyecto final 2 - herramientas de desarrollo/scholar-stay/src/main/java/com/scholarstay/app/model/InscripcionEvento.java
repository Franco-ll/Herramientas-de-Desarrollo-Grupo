package com.scholarstay.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones_eventos")
public class InscripcionEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;


    private LocalDateTime fechaInscripcion;


    public InscripcionEvento() {
        this.fechaInscripcion = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }


    public Usuario getUsuario() {
        return usuario;
    }


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public Evento getEvento() {
        return evento;
    }


    public void setEvento(Evento evento) {
        this.evento = evento;
    }


    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }


}