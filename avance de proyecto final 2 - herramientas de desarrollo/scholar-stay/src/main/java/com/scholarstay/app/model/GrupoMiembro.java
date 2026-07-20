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
import jakarta.persistence.UniqueConstraint;

/**
 * Representa la membresía de un usuario en un grupo.
 * Un usuario puede tener dos relaciones con un grupo:
 *   - MIEMBRO: participa activamente, puede publicar en el muro
 *   - SEGUIDOR: recibe novedades pero no puede publicar
 *   - ADMIN: puede administrar el grupo (invitar, remover, editar)
 */
@Entity
@Table(name = "grupo_miembros",
       uniqueConstraints = @UniqueConstraint(columnNames = {"grupo_id", "usuario_id"}))
public class GrupoMiembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Rol dentro del grupo: MIEMBRO, SEGUIDOR, ADMIN
    @Column(nullable = false)
    private String rol = "MIEMBRO";

    // Fecha en que el usuario se unió o empezó a seguir
    @Column(nullable = false)
    private LocalDateTime fechaUnion;

    public GrupoMiembro() {
        this.fechaUnion = LocalDateTime.now();
    }

    public GrupoMiembro(Grupo grupo, Usuario usuario, String rol) {
        this.grupo = grupo;
        this.usuario = usuario;
        this.rol = rol;
        this.fechaUnion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public LocalDateTime getFechaUnion() { return fechaUnion; }
    public void setFechaUnion(LocalDateTime fechaUnion) { this.fechaUnion = fechaUnion; }
}
