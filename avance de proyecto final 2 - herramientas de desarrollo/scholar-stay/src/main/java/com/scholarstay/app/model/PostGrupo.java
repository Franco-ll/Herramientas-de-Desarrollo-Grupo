package com.scholarstay.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa una publicación en el muro de debate de un grupo.
 * Los posts son visibles para todos los miembros del grupo.
 * Solo los MIEMBRO y ADMIN pueden publicar (los SEGUIDOR solo leen).
 */
@Entity
@Table(name = "posts_grupo")
public class PostGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    // Contenido del post — sanitizado antes de guardar (anti-XSS)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    // Contador de likes — se incrementa/decrementa con las acciones del usuario
    @Column(nullable = false)
    private Integer likes = 0;

    // Contador de respuestas — se incrementa al agregar comentarios
    @Column(nullable = false)
    private Integer respuestas = 0;

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;

    public PostGrupo() {
        this.fechaPublicacion = LocalDateTime.now();
        this.likes = 0;
        this.respuestas = 0;
    }

    public PostGrupo(Grupo grupo, Usuario autor, String contenido) {
        this.grupo = grupo;
        this.autor = autor;
        this.contenido = contenido;
        this.fechaPublicacion = LocalDateTime.now();
        this.likes = 0;
        this.respuestas = 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }

    public Integer getRespuestas() { return respuestas; }
    public void setRespuestas(Integer respuestas) { this.respuestas = respuestas; }

    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}
